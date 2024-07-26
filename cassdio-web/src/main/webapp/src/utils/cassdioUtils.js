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

    convertRowHeader: (columnList, rowHeader) => {
        const convertedMap = CassdioUtils.columnListSortValueMap(columnList);

        const result = []
        for (const header of rowHeader) {
            const key = CassdioUtils.makeRowKey(header.keyspace, header.table, header.columnName)

            const convertedColumnInfo = convertedMap[key];
            result.push(convertedColumnInfo)

        }

        return result;
    },

    columnListSortValueMap: (columnList) => {
        if (!columnList || columnList.rows.length <= 0) {
            return [];
        }

        const rows = columnList.rows;

        const kindSort = {
            'partition_key': 0,
            'clustering': 1,
            'regular': 2,
            'unknown': 3,
        }

        const temp = {}

        for (const row of rows) {

            temp[CassdioUtils.makeRowKey(row.keyspace_name, row.table_name, row.column_name)] = {
                ...row,
                sortValue: `${kindSort[row.kind]}-${row.position}`
            }
        }

        return temp;
    },

    makeRowKey: (keyspace, table, column) => {
        return `${keyspace}.${table}.${column}`
    },

    columnListSorting: (columnList) => {
        if (!columnList || columnList.rows.length <= 0) {
            return [];
        }

        const rows = columnList.rows;

        const kindSort = {
            'partition_key': 0,
            'clustering': 1,
            'regular': 2,
            'unknown': 3,
        }

        const temp = []

        for (const row of rows) {
            temp.push({
                ...row,
                sortValue: `${kindSort[row.kind]}-${row.position}`
            })
        }

        temp.sort(function (a, b) {
            if (a.sortValue < b.sortValue) {
                return -1;
            }
            if (a.sortValue > b.sortValue) {
                return 1;
            }
            return 0;
        })

        return {
            ...columnList,
            rows: temp
        };
    }
};
