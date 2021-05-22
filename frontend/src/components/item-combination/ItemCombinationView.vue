<template>
  <ul>
    <li v-for="(item,index) in questionDetails.items" :key="index">
      <span v-html="convertMarkDown(item.content)" />
      <ul>
        <li class="show-option-list" v-for="(option, index) in item.options" :key="index">
          <span
            v-if="option.correct"
            v-html="convertMarkDown('**[★]** ' + option.content)"
            v-bind:class="[option.correct ? 'font-weight-bold' : '']"
          />
          <span
            v-else
            v-html="convertMarkDown(option.content)"
          />
        </li>
      </ul>
    </li>
  </ul>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';

@Component
export default class ItemCombinationView extends Vue {
  @Prop() readonly questionDetails!: ItemCombinationQuestionDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss">
.show-option-list{
  display: inline-block;
  list-style-type: none;
  margin-left: 10px;
  margin-right: 10px;
}
</style>
