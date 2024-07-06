import React, {createContext, Dispatch, useContext, useReducer} from "react";

type CassdioState = {
    bootstrapLoading: boolean,
    systemAvailable: boolean,
    toasts?: any[],
};

type Action =
    | { type: "SET_BOOTSTRAP_LOADING"; bootstrapLoading: boolean; }
    | { type: "SET_SYSTEM_AVAILABLE"; systemAvailable: boolean; }

    | { type: "SET_TOAST"; message: string, delay: number }
    | { type: "REMOVE_TOAST_ITEM"; id: number }

type CassdioDispatch = Dispatch<Action>;

const CassdioStateContext = createContext<CassdioState | null>(null);
const CassdioDispatchContext = createContext<CassdioDispatch | null>(null);

export function CassdioProvider({children}: { children: React.ReactNode }) {
    const [state, dispatch] = useReducer(reducer, {
        bootstrapLoading: false,
        systemAvailable: false,
        toasts: undefined,
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
        // case "SET_TOASTS":
        //     return {
        //         ...state,
        //         toasts: action.toasts
        //     };
        case "SET_TOAST":
            const arr = state.toasts ? [...state.toasts] : [];
            arr.push({
                id : new Date().getTime(),
                message: action.message,
                delay: action.delay,
            });
            return {
                ...state,
                toasts: arr
            };
        case "REMOVE_TOAST_ITEM":
            const arr2 = state.toasts;
            //ID 추가 해서 그거 지울 수 있게 해야함

            if (!arr2) {
                return {
                    ...state,
                };
            }

            return {
                ...state,
                //양이 많을 경우 누락됨.
                toasts: arr2.filter(info => info.id !== action.id)
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
