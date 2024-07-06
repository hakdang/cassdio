import React, {createContext, Dispatch, useContext, useReducer} from "react";

type CadioState = {
    bootstrapLoading: boolean,
    systemAvailable: boolean,
    toasts?: any[],
};

type Action =
    | { type: "SET_BOOTSTRAP_LOADING"; bootstrapLoading: boolean; }
    | { type: "SET_SYSTEM_AVAILABLE"; systemAvailable: boolean; }

    | { type: "SET_TOAST"; message: string, delay: number }
    | { type: "REMOVE_TOAST_ITEM"; toastIndex: number }

type CadioDispatch = Dispatch<Action>;

const CadioStateContext = createContext<CadioState | null>(null);
const CadioDispatchContext = createContext<CadioDispatch | null>(null);

export function CadioProvider({children}: { children: React.ReactNode }) {
    const [state, dispatch] = useReducer(reducer, {
        bootstrapLoading: false,
        systemAvailable: false,
        toasts: undefined,
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
        // case "SET_TOASTS":
        //     return {
        //         ...state,
        //         toasts: action.toasts
        //     };
        case "SET_TOAST":
            const arr = state.toasts ? [...state.toasts] : [];
            arr.push({
                //TODO : 아이디 추가
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

            arr2.splice(action.toastIndex, 1)
            return {
                ...state,
                toasts: arr2
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
