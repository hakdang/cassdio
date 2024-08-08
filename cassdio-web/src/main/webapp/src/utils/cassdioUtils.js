export const CassdioUtils = {
    renderData: (data) => {
        if (typeof data === "object") {
            if (Array.isArray(data)) {
                return data.join(',');
            }

            return JSON.stringify(data);
        }

        return data;
    },
};
