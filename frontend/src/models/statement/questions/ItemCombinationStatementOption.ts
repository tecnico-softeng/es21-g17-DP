export default class ItemCombinationStatementOption {
  optionId!: number;
  content!: string;
  none!: boolean;

  constructor(jsonObj?: ItemCombinationStatementOption) {
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
      this.content = jsonObj.content;
      this.none = jsonObj.none;
    }
  }
}
