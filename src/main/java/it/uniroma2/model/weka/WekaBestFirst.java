package it.uniroma2.model.weka;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.enums.FeatureSel;
import weka.attributeSelection.BestFirst;
import weka.core.SelectedTag;

public class WekaBestFirst {

    private BestFirst bestFirstBackward;
    private BestFirst bestFirstForward;
    private BestFirst bestFirstBiDirectional;

    // best first feature selection based on 3 different strategies
    public WekaBestFirst() {
        List<BestFirst> bestFirstList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            BestFirst bestFirst = new BestFirst();
            bestFirst.setDirection(new SelectedTag(i, bestFirst.getDirection().getTags()));
            bestFirstList.add(bestFirst);
        }
        bestFirstBackward = bestFirstList.get(0);
        bestFirstForward = bestFirstList.get(1);
        bestFirstBiDirectional = bestFirstList.get(2);
    }


    public BestFirst getBestFirst(FeatureSel featureSelection) {
        switch (featureSelection) {
            case BEST_FIRST_BACKWARD:
                return bestFirstBackward;
            case BEST_FIRST_FORWARD:
                return bestFirstForward;
            case BEST_FIRST_BI_DIRECTIONAL:
                return bestFirstBiDirectional;
            default:
                throw new IllegalArgumentException("Feature selection not supported");
        }
    }
}
