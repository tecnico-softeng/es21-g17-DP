import ItemCombinationStatementOption from '@/models/statement/questions/ItemCombinationStatementOption';

export default class ItemCombinationItemStatement {
  sequence!: number;
  content!: string;
  options: ItemCombinationStatementOption[] = [];

  constructor(jsonObj?: ItemCombinationItemStatement) {
    if (jsonObj) {
      this.sequence = jsonObj.sequence || this.sequence;
      this.content = jsonObj.content;
      this.options = jsonObj.options;
    }
  }
}
