<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema/> > Relatório Alunos com NEE por Processo Seletivo</h2>
	<h:form id="form">
	<a4j:keepAlive beanName="relatorioNee"  />
		<table class="formulario" id="dadosRelatorio"  style="width:60%;"> 
			<caption class="listagem">Dados do Relatório</caption>
			<tbody>
				<tr align="center">
					<td width="2%">
						<h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioNee.filtroAnoIngresso}" id="checkAnoIngresso" />
					</td>
					<td style="text-align: left" width="200px">Ano de Ingresso:</td>
					<td align="left" width="50%">
						<h:inputText value="#{relatorioNee.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" onfocus="$('form:checkAnoIngresso').checked = true;"  />
					</td>
				</tr>
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioNee.filtroProcessoSeletivo}" id="checkProcessoSeletivo" />
					</td>
					<td style="text-align: left" width="130px">Processo Seletivo:</td>
					<td>
						<h:selectOneMenu id="selectPSVestibular" value="#{relatorioNee.processoSeletivo.id}"  onfocus="$('form:checkProcessoSeletivo').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems id="listaAnosReferencia" value="#{convocacaoVestibular.processoSeletivoVestibularCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>	
				<tr>
					<td>
						<h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioNee.filtroTipoNecessidade}" id="checkTipoNecessidade" />
					</td>
					<td style="text-align: left">Tipo de Necessidade Educacional Especial:</td>
					<td>
						<h:selectOneMenu id="selectTipoNee" value="#{relatorioNee.tipoNecessidade.id}" onfocus="$('form:checkTipoNecessidade').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{tipoNecessidadeEspecialMBean.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td>
						<h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioNee.filtroCurso}" id="checkCurso" />
					</td>
					<td style="text-align: left">Curso:</td>
					<td align="left" width="50%">
						<h:inputText value="#{relatorioNee.nomeCurso}" maxlength="255" size="60" id="curso" onfocus="$('form:checkCurso').checked = true;"/>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioNee.relatorioAlunosPorVestibular}" id="btnGerar"/> 
						<h:commandButton value="Cancelar" action="#{relatorioNee.cancelar}" onclick="#{confirm}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
  <br />
 
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>