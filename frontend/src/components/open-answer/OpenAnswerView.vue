<template>
  <ul class="open-answer-view">
      <v-textarea
        name="open-answer-view-correct"
        disabled
        label="Correct Answer"
        v-bind:value=[questionDetails.correctAnswer]
    ></v-textarea>
  </ul>
</template>

<script lang="ts">
import OpenAnswerAnswerDetails from '@/models/management/questions/OpenAnswerAnswerDetails';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';

@Component({
  components: {
  }
})
export default class OpenAnswerView extends Vue {
  @Prop() readonly questionDetails!: OpenAnswerQuestionDetails;
  @Prop() readonly answerDetails?: OpenAnswerAnswerDetails;
}
</script>

<style lang="scss">
</style>
