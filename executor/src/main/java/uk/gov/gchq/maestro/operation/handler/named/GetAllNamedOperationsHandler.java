/*
 * Copyright 2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.maestro.operation.handler.named;

import org.apache.commons.collections.CollectionUtils;

import uk.gov.gchq.koryphe.util.IterableUtil;
import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.commonutil.iterable.WrappedCloseableIterable;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JsonSerialisationUtil;
import uk.gov.gchq.maestro.named.operation.NamedOperationDetail;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.handler.OutputOperationHandler;
import uk.gov.gchq.maestro.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.maestro.util.ExecutorPropertiesUtil;

import java.util.List;
import java.util.function.Function;

/**
 * Operation Handler for GetAllNamedOperations
 */
public class GetAllNamedOperationsHandler implements OutputOperationHandler<CloseableIterable<NamedOperationDetail>> {
    private final NamedOperationCache cache;

    public GetAllNamedOperationsHandler() {
        this(new NamedOperationCache());
    }

    public GetAllNamedOperationsHandler(final NamedOperationCache cache) {
        this.cache = cache;
    }

    /**
     * Retrieves all the Named Operations that a user is allowed to see. As the expected behaviour is to bring back a
     * summary of each operation, the simple flag is set to true. This means all the details regarding access roles and
     * operation chain details are not included in the output.
     *
     * @param operation the {@link uk.gov.gchq.maestro.operation.Operation} to be
     *                  executed
     * @param context   the operation chain context, containing the user who executed the operation
     * @param executor  the {@link Executor} the operation should be run on
     * @return an iterable of NamedOperations
     * @throws OperationException thrown if the cache has not been initialized in the operation declarations file
     */
    @Override
    public CloseableIterable<NamedOperationDetail> doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        final CloseableIterable<NamedOperationDetail> ops =
                cache.getAllNamedOperations(context.getUser(),
                        ExecutorPropertiesUtil.getAdminAuth(executor.getConfig().getProperties()));
        return new WrappedCloseableIterable<>(IterableUtil.map(ops, new AddInputType()));
    }

    private static class AddInputType implements Function<NamedOperationDetail, NamedOperationDetail> {
        @Override
        public NamedOperationDetail apply(final NamedOperationDetail namedOp) {
            if (null != namedOp && null == namedOp.getInputType()) {
                try {
                    final List<Operation> opList = namedOp.getOperationChainWithDefaultParams().getOperations();
                    if (CollectionUtils.isNotEmpty(opList)) {
                        final Operation firstOp = opList.get(0);
                        if (firstOp.containsKey("Input") ) {
                            namedOp.setInputType(JsonSerialisationUtil.getSerialisedFieldClasses(firstOp.getClass().getName()).get("input"));
                        }
                    }
                } catch (final Exception e) {
                    // ignore - just don't add the input type
                }
            }
            return namedOp;
        }
    }

}
