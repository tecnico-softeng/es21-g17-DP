import StatementOption from '@/models/statement/StatementOption';
import { _ } from 'vue-underscore';

export default class OpenAnswerSpotAnswerStatement {
  sequence!: number;
  optionId!: number | undefined;
  optionSequence!: number | undefined;

  constructor(jsonObj?: OpenAnswerSpotAnswerStatement) {
    if (jsonObj) {
      this.sequence = jsonObj.sequence;
      this.optionId = jsonObj.optionId;
      this.optionSequence = jsonObj.optionSequence;
    }
  }
}
