import Option from '@/models/management/Option';
import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes, convertToLetter } from '@/services/QuestionHelpers';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';

export default class OpenAnswerAnswerType extends AnswerDetails {
    correctAnswer: string = '';
  
    constructor(jsonObj?: OpenAnswerAnswerType) {
      super(QuestionTypes.OpenAnswer);
      if (jsonObj) {
          this.correctAnswer = jsonObj.correctAnswer;
      }
    }
  
    isCorrect(questionDetails: OpenAnswerQuestionDetails): boolean {
        return this.correctAnswer.length === questionDetails.correctAnswer.length;
    }
  
    answerRepresentation(questionDetails: OpenAnswerQuestionDetails): string {
        return '';
    }
}

