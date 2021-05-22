import ItemCombinationOption from '@/models/management/questions/ItemCombinationOption';

export default class ItemCombinationItem {
  id: number | null = null;
  sequence: number = 1;
  options: ItemCombinationOption[] = [
    new ItemCombinationOption(),
    new ItemCombinationOption()
  ];
  content: string = '';

  constructor(jsonObj?: ItemCombinationItem) {
    this.options[0].none = true;
    this.options[0].sequence = 1;
    this.options[0].content = 'None';
    this.options[1].content = 'Option 1';
    this.options[1].sequence = 2;

    if (jsonObj) {
      this.id = jsonObj.id || this.id;
      this.sequence = jsonObj.sequence || this.sequence;
      this.options = jsonObj.options.map(
        (option: ItemCombinationOption) => new ItemCombinationOption(option)
      );

      this.content = jsonObj.content;
    }
  }

  setAsNew(): void {
    this.id = null;
    this.options.forEach(option => {
      option.id = null;
    });
  }
}
