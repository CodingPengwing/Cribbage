package cribbage.Score;

import cribbage.Cribbage;

/** A Factory for creating Scorer Composites */
public class ScorerCompositeFactory {
    private static ScorerCompositeFactory instance = null;

    private static Scorer scorerCompositeShow = null;
    private static Scorer scorerCompositePlay = null;

    // Empty Singleton constructor
    private ScorerCompositeFactory() {}

    /**
     * Singleton getInstance method
     * @return The Factory instance
     */
    public static ScorerCompositeFactory getInstance() {
        if (instance == null) instance = new ScorerCompositeFactory();
        return instance;
    }

    // Factory methods -------------------------------------------------------------------------------------------------
    /** @return A Composite for scoring during the show phase */
    private Scorer getScorerCompositeShow() {
        if (scorerCompositeShow == null) {
            ScorerComposite composite = new ScorerComposite();
            ScorerFactory scorerFactory = ScorerFactory.getInstance();
            composite.addScorer(scorerFactory.getMilestoneScorer());
            composite.addScorer(scorerFactory.getRunScorer());
            composite.addScorer(scorerFactory.getPairScorer());
            composite.addScorer(scorerFactory.getFlushScorer());
            composite.addScorer(scorerFactory.getJackOfStarterSuitScorer());
            scorerCompositeShow = composite;
        }
        return scorerCompositeShow;
    }

    /** @return A Composite for scoring during the Play phase */
    private Scorer getScorerCompositePlay() {
        if (scorerCompositePlay == null) {
            ScorerComposite composite = new ScorerComposite();
            ScorerFactory scorerFactory = ScorerFactory.getInstance();
            composite.addScorer(scorerFactory.getMilestoneScorerPlay());
            composite.addScorer(scorerFactory.getRunScorerPlay());
            composite.addScorer(scorerFactory.getPairScorerPlay());
            scorerCompositePlay = composite;
        }
        return scorerCompositePlay;
    }

    /** @return A Composite for scoring the current phase of the game */
    public Scorer getScorerComposite() {
        ScorerFactory scorerFactory = ScorerFactory.getInstance();
        Cribbage cribbage = Cribbage.getInstance();
        switch (cribbage.getGamePhase()) {
            case START: return scorerFactory.getStarterScorer();
            case PLAY: case PLAY_SCORE: return getScorerCompositePlay();
            case PLAY_SCORE_GO: return scorerFactory.getGoScorer();
            case SHOW: case SHOW_SCORE: return getScorerCompositeShow();
            default:
                System.err.println("Error in getScorerComposite(): no matching game phase.");
                System.exit(1);
        }
        return null;
    }
}
