import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CodeFillInSpotAnswerStatement from './CodeFillInSpotAnswerStatement';

export default class ItemCombinationStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctOptions!: CodeFillInSpotAnswerStatement[];

  constructor(jsonObj?: ItemCombinationStatementCorrectAnswerDetails) {
    super(QuestionTypes.CodeFillIn);
    if (jsonObj) {
      this.correctOptions = jsonObj.correctOptions || [];
    }
  }
}

