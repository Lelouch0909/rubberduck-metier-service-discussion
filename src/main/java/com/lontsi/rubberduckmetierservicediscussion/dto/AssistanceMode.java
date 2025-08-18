package com.lontsi.rubberduckmetierservicediscussion.dto;

public enum AssistanceMode {

    /**
     * L'utilisateur apprend par lui-même, l'assistant propose seulement des ressources ou pistes à explorer.
     */
    DECOUVERTE,

    /**
     * L'assistant répond aux questions, explique des concepts ou corrige des erreurs,
     * mais ne donne pas la solution complète.
     */
    EXPLICATIF,

    /**
     * L'assistant guide l'utilisateur étape par étape, en posant des questions ou en proposant des mini-challenges.
     */
    TUTORIEL_GUIDE,

    /**
     * L'utilisateur propose une solution, l'assistant aide à la corriger et explique chaque étape de correction.
     */
    CORRECTION_INTERACTIVE,

    /**
     * L'assistant fournit la solution, puis détaille chaque étape et encourage l'utilisateur à poser des questions.
     */
    SOUTIEN_TOTAL,

    /**
     * L'utilisateur choisit son niveau d'autonomie ou le mode d'aide souhaité.
     */
    PERSONNALISE

}
