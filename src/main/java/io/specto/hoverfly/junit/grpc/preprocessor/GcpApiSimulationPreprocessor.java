package io.specto.hoverfly.junit.grpc.preprocessor;

import io.specto.hoverfly.junit.core.SimulationPreprocessor;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.Simulation;

/**
 * An out-of-box {@link SimulationPreprocessor} for removing GCP authentication request body from matching.
 * The OAuth request body contains time-sensitive data, and does not exactly match on previous captured data.
 */
public class GcpApiSimulationPreprocessor implements SimulationPreprocessor {

    @Override
    public void accept(Simulation simulation) {
        simulation.getHoverflyData().getPairs().stream()
                .map(RequestResponsePair::getRequest)
                .filter(request -> request.getDestination().get(0).getValue().toString().contains("oauth2.googleapis.com"))
                .forEach(pair -> pair.getBody().clear());
    }
}
