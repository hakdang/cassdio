import React, {createContext, Dispatch, useContext, useReducer} from "react";

type CassdioState = {
    bootstrapLoading: boolean,
    systemAvailable: boolean,
    consistencyLevels: [],
    defaultConsistencyLevel: {},
};

type Action =
    | { type: "SET_BOOTSTRAP_LOADING"; bootstrapLoading: boolean; }
    | { type: "SET_SYSTEM_AVAILABLE"; systemAvailable: boolean; }
    | { type: "SET_BOOTSTRAP"; consistencyLevels: []; defaultConsistencyLevel: object; }

type CassdioDispatch = Dispatch<Action>;

const CassdioStateContext = createContext<CassdioState | null>(null);
const CassdioDispatchContext = createContext<CassdioDispatch | null>(null);

export function CassdioProvider({children}: { children: React.ReactNode }) {
    const [state, dispatch] = useReducer(reducer, {
        bootstrapLoading: false,
        systemAvailable: false,
        consistencyLevels: [],
        defaultConsistencyLevel: {},
    });

    return (
        <CassdioStateContext.Provider value={state}>
            <CassdioDispatchContext.Provider value={dispatch}>
                {children}
            </CassdioDispatchContext.Provider>
        </CassdioStateContext.Provider>
    );
}

function reducer(state: CassdioState, action: Action): CassdioState {
    switch (action.type) {
        case "SET_BOOTSTRAP_LOADING":
            return {
                ...state,
                bootstrapLoading: action.bootstrapLoading
            };
        case "SET_SYSTEM_AVAILABLE":
            return {
                ...state,
                systemAvailable: action.systemAvailable
            };
        case "SET_BOOTSTRAP":
            return {
                ...state,
                consistencyLevels: action.consistencyLevels,
                defaultConsistencyLevel: action.defaultConsistencyLevel,
            };

        default:
            throw new Error("Unhandled action");
    }
}

export function useCassdioState() {
    const state = useContext(CassdioStateContext);
    if (!state) throw new Error("Cannot find CassdioProvider");
    return state;
}

export function useCassdioDispatch() {
    const dispatch = useContext(CassdioDispatchContext);
    if (!dispatch) throw new Error("Cannot find CassdioProvider");
    return dispatch;
}
