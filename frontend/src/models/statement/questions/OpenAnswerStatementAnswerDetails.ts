import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import OpenAnswerStatementCorrectAnswerDetails from '@/models/statement/questions/OpenAnswerStatementCorrectAnswerDetails';
import OpenAnswerSpotAnswerStatement from './OpenAnswerSpotAnswerStatement';

export default class OpenAnswerStatementAnswerDetails extends StatementAnswerDetails {
  public selectedOptions!: OpenAnswerSpotAnswerStatement[];

  constructor(jsonObj?: OpenAnswerStatementAnswerDetails) {
    super(QuestionTypes.OpenAnswer);
    if (jsonObj) {
      this.selectedOptions = jsonObj.selectedOptions || [];
    }
  }

  isQuestionAnswered(): boolean {
    return this.selectedOptions != null && this.selectedOptions.length > 0;
  }

  isAnswerCorrect(
    correctAnswerDetails: OpenAnswerStatementCorrectAnswerDetails): boolean {
        var x =
            correctAnswerDetails.correctAnswer != null &&
            this.selectedOptions.length === correctAnswerDetails.correctAnswer.length;
    return x;
  }
}
