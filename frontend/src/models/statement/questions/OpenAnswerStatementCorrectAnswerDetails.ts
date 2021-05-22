import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenAnswerStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctAnswer: string | null = null;

  constructor(jsonObj?: OpenAnswerStatementCorrectAnswerDetails) {
    super(QuestionTypes.OpenAnswer);
    if (jsonObj) {
      this.correctAnswer = jsonObj.correctAnswer;
    }
  }
}
