﻿namespace Gis {
    export const reportLayerFormula = {
        count: "count",
        sum: "sum",
        average: "average"
    }

    export function reportFormulaCheckParams(reportInfo: ILayerReportSettings):boolean {
        let result = false;
        switch (reportInfo.reportFormula) {
            case reportLayerFormula.count:
                result = reportCountCheckParams(reportInfo);
                break;
            case reportLayerFormula.sum:
                result = reportSumCheckParams(reportInfo);
                break;
            case reportLayerFormula.average:
                result = reportAverageCheckParams(reportInfo);
                break;
            default:
                result = false;
        }
        return result;
    }

    export function reportFormulaProcessData(reportInfo: Gis.ILayerReportSettings, repFeature: ol.Feature, dataFeatures: Array<ol.Feature>) {
        switch (reportInfo.reportFormula) {
            case reportLayerFormula.count:
                reportCount(reportInfo, repFeature, dataFeatures);
                break;
            case reportLayerFormula.sum:
                reportSum(reportInfo, repFeature, dataFeatures);
                break;
            case reportLayerFormula.average:
                reportAverage(reportInfo, repFeature, dataFeatures);
                break;
            default:
                
        }
    }

    //
    export function reportCountCheckParams(reportInfo: Gis.ILayerReportSettings): boolean {
        return ("result" in reportInfo.reportColumns);
    }
    export function reportCount(reportInfo: Gis.ILayerReportSettings, repFeature: ol.Feature, dataFeatures: Array<ol.Feature>) {
        try {
            let result = 0;
            dataFeatures.forEach((sitem) => {
                if (sitem["searchFilterOut"] === 'false') {
                    result++;
                }
            });
            repFeature.set(reportInfo.reportColumns['result'], result)
        } catch (e) {
            throw new Error(`count ${repFeature.getId()} : ${e.message}`);
        }
    }
    //

    //
    export function reportSumCheckParams(reportInfo: Gis.ILayerReportSettings): boolean{
        return ("result" in reportInfo.reportColumns) && ('input1' in reportInfo.dataColumns);
    }
    export function reportSum(reportInfo: Gis.ILayerReportSettings, repFeature: ol.Feature, dataFeatures: Array<ol.Feature>) {
        try {
            let result = 0;
            dataFeatures.forEach((sitem) => {
                if (sitem["searchFilterOut"] === 'false') {
                    let tmpVal = sitem.get(reportInfo.dataColumns['input1']);
                    if (tmpVal !== undefined && !isNaN(tmpVal)) {
                        result += tmpVal;
                    }
                }
            });
            repFeature.set(reportInfo.reportColumns['result'], result)
        } catch (e) {
            throw new Error(`sum ${repFeature.getId()} : ${e.message}`);
        }
    }

    //
    export function reportAverageCheckParams(reportInfo: Gis.ILayerReportSettings): boolean {
        return ("result" in reportInfo.reportColumns) && ('input1' in reportInfo.dataColumns);
    }
    export function reportAverage(reportInfo: Gis.ILayerReportSettings, repFeature: ol.Feature, dataFeatures: Array<ol.Feature>) {
        try {
            let result = 0;
            let count = 0
            dataFeatures.forEach((sitem) => {
                if (sitem["searchFilterOut"] === 'false') {
                    let tmpVal = sitem.get(reportInfo.dataColumns['input1']);
                    if (tmpVal !== undefined && !isNaN(tmpVal)) {
                        result += tmpVal;
                        count++;
                    }
                }
            });
            if (count > 1) {
                result = result / count;
            }
            repFeature.set(reportInfo.reportColumns['result'], result)
        } catch (e) {
            throw new Error(`sum ${repFeature.getId()} : ${e.message}`);
        }
    }
}