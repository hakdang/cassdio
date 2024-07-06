import React, {createContext, Dispatch, useContext, useReducer} from "react";

type ClusterState = {
    keyspaceList: [],
    keyspaceListLoading: boolean,
    keyspaceGeneralNames: [],
    keyspaceSystemNames: [],
    keyspaceNamesLoading: boolean,
};

type Action =
    | { type: "SET_KEYSPACE_GENERAL_NAMES"; keyspaceNames: []; }
    | { type: "SET_KEYSPACE_SYSTEM_NAMES"; keyspaceNames: []; }
    | { type: "SET_KEYSPACE_NAMES_LOADING"; loading: boolean; }
    | { type: "SET_KEYSPACE_LIST"; keyspaceList: []; }
    | { type: "SET_KEYSPACE_LIST_LOADING"; loading: boolean; }

type ClusterDispatch = Dispatch<Action>;

const ClusterStateContext = createContext<ClusterState | null>(null);
const ClusterDispatchContext = createContext<ClusterDispatch | null>(null);

export function ClusterProvider({children}: { children: React.ReactNode }) {
    const [state, dispatch] = useReducer(reducer, {
        keyspaceList: [],
        keyspaceListLoading: false,
        keyspaceGeneralNames: [],
        keyspaceSystemNames: [],
        keyspaceNamesLoading: false
    });

    return (
        <ClusterStateContext.Provider value={state}>
            <ClusterDispatchContext.Provider value={dispatch}>
                {children}
            </ClusterDispatchContext.Provider>
        </ClusterStateContext.Provider>
    );
}

function reducer(state: ClusterState, action: Action): ClusterState {
    switch (action.type) {
        case "SET_KEYSPACE_LIST":
            return {
                ...state,
                keyspaceList: action.keyspaceList
            };
        case "SET_KEYSPACE_LIST_LOADING":
            return {
                ...state,
                keyspaceListLoading: action.loading,
            };
        case "SET_KEYSPACE_GENERAL_NAMES":
            return {
                ...state,
                keyspaceGeneralNames: action.keyspaceNames
            };
        case "SET_KEYSPACE_SYSTEM_NAMES":
            return {
                ...state,
                keyspaceSystemNames: action.keyspaceNames
            };

        case "SET_KEYSPACE_NAMES_LOADING":
            return {
                ...state,
                keyspaceNamesLoading: action.loading,
            };


        default:
            throw new Error("Unhandled action");
    }
}

export function useClusterState() {
    const state = useContext(ClusterStateContext);
    if (!state) throw new Error("Cannot find ClusterProvider");
    return state;
}

export function useClusterDispatch() {
    const dispatch = useContext(ClusterDispatchContext);
    if (!dispatch) throw new Error("Cannot find ClusterProvider");
    return dispatch;
}
