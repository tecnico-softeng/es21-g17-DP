export default class ItemCombinationOption {
  id: number | null = null;
  sequence!: number | null;
  content: string = '';
  correct: boolean = false;
  none: boolean = false;

  constructor(jsonObj?: ItemCombinationOption) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.sequence = jsonObj.sequence;
      this.content = jsonObj.content;
      this.correct = jsonObj.correct;
      this.none = jsonObj.none;
    }
  }
}
