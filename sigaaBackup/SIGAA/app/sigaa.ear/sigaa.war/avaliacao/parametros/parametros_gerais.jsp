<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Par�metros da Avalia��o Institucional </h2>

	<h:form id="form">

		<table class="formulario" width="90%">
			<caption>Informe os Par�metros Gerais da Avalia��o Institucional</caption>
			<tbody>
				<tr>
					<th width="30%" class="required">Auto-Avalia��o pelo Docente:</th>
					<td>
						<h:selectOneRadio value="#{parametrosAvaliacaoInstitucionalBean.avaliacaoDocente}" id="checkAvaliacaoDocente" > 
							<f:selectItems value="#{parametrosAvaliacaoInstitucionalBean.simNao}"  />
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<th width="30%" class="required">Exibir Coment�rios no Relat�rio Anal�tico:</th>
					<td>
						<h:selectOneRadio value="#{parametrosAvaliacaoInstitucionalBean.comentariosLiberados}" id="checkComentariosLiberados" > 
							<f:selectItems value="#{parametrosAvaliacaoInstitucionalBean.simNao}"  />
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<th width="30%" class="required">Grupo de Perguntas (Dimens�o) que define a M�dia Geral do Docente:</th>
					<td>
						<h:selectOneMenu value="#{parametrosAvaliacaoInstitucionalBean.idGrupo}" id="idGrupoPergunta" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>  
							<f:selectItems value="#{relatorioAvaliacaoMBean.grupoPerguntasAvaliaDocenteComboBox}"  />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Alterar Par�metros" action="#{ parametrosAvaliacaoInstitucionalBean.cadastrarParametrosGerais }" id="cadastrar" /> 
						<h:commandButton value="Cancelar" action="#{ parametrosAvaliacaoInstitucionalBean.cancelar }" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>