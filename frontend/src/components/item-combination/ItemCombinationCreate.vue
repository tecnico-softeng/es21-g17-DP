<template>
  <div class="items-options-board">
    <v-row>
      <v-col class="text-center" cols="6">
        Items
      </v-col>

      <v-col class="text-center" cols="6">
        Options
      </v-col>
    </v-row>
    <v-row>
      <v-col class="item-list" cols="6">
        <v-row
          v-for="(item, index) in sQuestionDetails.items"
          :key="index"
          data-cy="questionItemsInput"
        >
          <v-col cols="11">
            <v-textarea
              v-model="item.content"
              :label="`Item ${index + 1}`"
              :data-cy="`Item${index + 1}`"
              rows="1"
              auto-grow
            ></v-textarea>
            <ItemCombinationItemEditor
              :item0.sync="sQuestionDetails.items[0]"
              :item.sync="item"
            />
          </v-col>

          <v-col cols="1" v-if="sQuestionDetails.items.length > 1">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  :data-cy="`Delete${index + 1}`"
                  small
                  class="ma-1 action-button"
                  v-on="on"
                  @click="removeItem(index)"
                  color="red"
                  >close</v-icon
                >
              </template>
              <span>Remove Item</span>
            </v-tooltip>
          </v-col>
        </v-row>
      </v-col>

      <v-col class="option-list" cols="6">
        <v-row
          v-for="(option, index) in sQuestionDetails.items[0].options"
          :key="index"
          data-cy="questionOptionInput"
        >
          <v-col cols="11" v-if="index > 0">
            <v-textarea
              v-model="option.content"
              :label="`Option ${index}`"
              :data-cy="`Option${index}`"
              rows="1"
              auto-grow
            ></v-textarea>
          </v-col>

          <v-col cols="1" v-if="index > 1">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  :data-cy="`Delete${index + 1}`"
                  small
                  class="ma-1 action-button"
                  v-on="on"
                  @click="removeOption(index)"
                  color="red"
                  >close</v-icon
                >
              </template>
              <span>Remove Option</span>
            </v-tooltip>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
    <v-row>
      <v-btn
        class="ma-auto"
        color="deep purple"
        @click="addItem"
        data-cy="addItemCombinationItem"
        >Add Item</v-btn
      >

      <v-btn
        class="ma-auto"
        color="blue darken-1"
        @click="addOption"
        data-cy="addItemCombinationOption"
        >Add Option</v-btn
      >
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue, PropSync, Prop } from 'vue-property-decorator';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import ItemCombinationItem from '@/models/management/questions/ItemCombinationItem';
import ItemCombinationOption from '@/models/management/questions/ItemCombinationOption';
import ItemCombinationItemEditor from '@/components/item-combination/ItemCombinationItemEditor.vue';

@Component({
  components: { ItemCombinationItemEditor }
})
export default class ItemCombinationCreate extends Vue {
  @PropSync('questionDetails', { type: ItemCombinationQuestionDetails })
  sQuestionDetails!: ItemCombinationQuestionDetails;

  copyOption(option: ItemCombinationOption)
  {
    let newOption = new ItemCombinationOption();
    newOption.sequence = option.sequence;
    newOption.none = option.none;
    newOption.correct = option.correct;
    newOption.content = option.content;
    return newOption;
  }

  addItem() {
    let newItem = new ItemCombinationItem();
    this.sQuestionDetails.items[0].options.forEach((option, index) =>{
      newItem.options[index] = this.copyOption(option);
      newItem.options[index].correct = false;
    })
    newItem.sequence = this.sQuestionDetails.items.length + 1;
    this.sQuestionDetails.items.push(newItem);
  }

  removeItem(index: number) {
    this.sQuestionDetails.items.splice(index, 1);
    this.sQuestionDetails.items.forEach((item,index) => {
      item.sequence = index + 1;
    })
  }

  removeOption(index: number) {
    this.sQuestionDetails.items.forEach(item => {
      item.options.splice(index, 1);
      item.options.forEach((option, index) =>{
        option.sequence = index + 1;
      });
    });
  }

  addOption() {
    let newOption = new ItemCombinationOption();
    let size = this.sQuestionDetails.items[0].options.length;
    newOption.content = 'Option ' + size;
    newOption.sequence = size + 1;
    this.sQuestionDetails.items.forEach(item => {
      item.options.push(this.copyOption(newOption));
    });
  }
}
</script>
