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

    rowHeaderSorting: (columnList, rowHeader) => {
        //const sortedColumnList = this.columnListSorting(columnList);

        for (const header of rowHeader) {
            console.log("header ", header)
        }
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
                'sortValue': `${kindSort[row.kind]}-${row.position}`
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

        return temp;
    }
};
