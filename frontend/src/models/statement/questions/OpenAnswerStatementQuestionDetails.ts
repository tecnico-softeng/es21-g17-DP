import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import OpenAnswerSpotStatement from '@/models/statement/questions/OpenAnswerSpotStatement';
import { _ } from 'vue-underscore';

export default class OpenAnswerStatementQuestionDetails extends StatementQuestionDetails {
  openAnswerSpot: OpenAnswerSpotStatement[] = [];

  constructor(jsonObj?: OpenAnswerStatementQuestionDetails) {
    super(QuestionTypes.OpenAnswer);
    if (jsonObj) {
      this.openAnswerSpot = jsonObj.openAnswerSpot
        ? jsonObj.openAnswerSpot.map(
            (fill: OpenAnswerSpotStatement) => new OpenAnswerSpotStatement(fill)
          )
        : this.openAnswerSpot;
    }
  }
}
