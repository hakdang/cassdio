import React, {createContext, Dispatch, useContext, useReducer} from "react";

type CadioState = {
    bootstrapLoading: boolean,
    systemAvailable: boolean,
};

type Action =
    | { type: "SET_BOOTSTRAP_LOADING"; bootstrapLoading: boolean; }
    | { type: "SET_SYSTEM_AVAILABLE"; systemAvailable: boolean; }

type CadioDispatch = Dispatch<Action>;

const CadioStateContext = createContext<CadioState | null>(null);
const CadioDispatchContext = createContext<CadioDispatch | null>(null);

export function CadioProvider({children}: { children: React.ReactNode }) {
    const [state, dispatch] = useReducer(reducer, {
        bootstrapLoading: false,
        systemAvailable: false,
    });

    return (
        <CadioStateContext.Provider value={state}>
            <CadioDispatchContext.Provider value={dispatch}>
                {children}
            </CadioDispatchContext.Provider>
        </CadioStateContext.Provider>
    );
}

function reducer(state: CadioState, action: Action): CadioState {
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
        default:
            throw new Error("Unhandled action");
    }
}

export function useCadioState() {
    const state = useContext(CadioStateContext);
    if (!state) throw new Error("Cannot find CadioProvider");
    return state;
}

export function useCadioDispatch() {
    const dispatch = useContext(CadioDispatchContext);
    if (!dispatch) throw new Error("Cannot find CadioProvider");
    return dispatch;
}
