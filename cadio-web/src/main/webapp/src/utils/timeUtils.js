import moment from "moment/moment";

export const DateUtils = {
    yyyyMMDDHHmmss: (timestamp) => {
        return moment(new Date(timestamp)).format('YYYY-MM-DD HH:mm:ss')
    }
};
