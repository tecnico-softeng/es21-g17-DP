import Option from '@/models/management/Option';
import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class MultipleChoiceQuestionDetails extends QuestionDetails {
  options: Option[] = [new Option(), new Option(), new Option(), new Option()];
  relevanceOfOptionsConsidered: boolean = false;

  constructor(jsonObj?: MultipleChoiceQuestionDetails) {
    super(QuestionTypes.MultipleChoice);
    if (jsonObj) {
      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
      this.relevanceOfOptionsConsidered = jsonObj.relevanceOfOptionsConsidered;
    }
  }

  setAsNew(): void {
    this.options.forEach(option => {
      option.id = null;
    });
  }
}
