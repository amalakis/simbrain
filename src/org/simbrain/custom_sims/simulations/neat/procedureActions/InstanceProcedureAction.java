package org.simbrain.custom_sims.simulations.neat.procedureActions;

import org.simbrain.custom_sims.simulations.neat.Agent;
import org.simbrain.custom_sims.simulations.neat.ProcedureAction;

public interface InstanceProcedureAction extends ProcedureAction {
    public void run(Agent a);
}
