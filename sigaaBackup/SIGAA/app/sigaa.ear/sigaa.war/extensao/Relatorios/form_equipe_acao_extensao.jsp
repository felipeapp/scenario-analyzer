<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>

function verificarSelect(select, id_check, id_label){
	var form = "formBuscaAtividade:";
	var indice = select.selectedIndex;
	var checkbox = $(form+id_check);
	if (indice > 0){
		checkbox.checked = true;
		$(form+id_label).value = select.options[indice].text;
	}else
		checkbox.checked = false;
}
</script>


<f:view>
	<h2><ufrn:subSistema /> &gt; Relatório de Membros da Equipe de Ações de Extensão </h2>
	<h:messages showDetail="true" />
	<h:form id="formBuscaAtividade">
		<table class="formulario" width="70%">
			<caption>Consultar</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Tipo da Ação:</th>
					<td><h:selectOneMenu id="buscaTipoAcao" value="#{relatorioEquipeExtensao.buscaTipoAtividade}" 
					onblur="javascript:verificarSelect(this,'checkBuscaTipoAcao','labelTipoAtividade');">
						<f:selectItem itemLabel="-- SELECIONE UM TIPO DE AÇÃO --" itemValue="-1"/>
	    	 			<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
	    	 		</h:selectOneMenu>
	    	 		</td>
					<td><h:selectBooleanCheckbox value="#{relatorioEquipeExtensao.checkBuscaTipoAcao}" id="checkBuscaTipoAcao" style="display:none"/> </td>
					<h:inputHidden id="labelTipoAtividade" value="#{relatorioEquipeExtensao.labelTipoAtividade}" />
		
				</tr>

				<tr>
					<th class="obrigatorio">Situação da Ação:</th>
					<td>
					<h:selectOneMenu id="buscaSituacaoAcao" value="#{relatorioEquipeExtensao.buscaSituacaoAtividade}" 
					onblur="javascript:verificarSelect(this,'checkBuscaSituacaoAcao','labelSituacaoAtividade');">
						<f:selectItem itemLabel="-- SELECIONE UMA SITUAÇÃO DA AÇÃO --" itemValue="-1"/>
		    	 		<f:selectItems value="#{atividadeExtensao.tipoSituacaoAtividadeCombo}" />
	 			 	</h:selectOneMenu>
	 			 	</td>
					<td><h:selectBooleanCheckbox value="#{relatorioEquipeExtensao.checkBuscaSituacaoAcao}" id="checkBuscaSituacaoAcao" style="display:none"/> </td>
					<h:inputHidden id="labelSituacaoAtividade" value="#{relatorioEquipeExtensao.labelSituacaoAtividade}" />
				</tr>

				<tr>
					<th class="obrigatorio">Tipo do Servidor:</th>
					<td>
					<h:selectOneMenu id="buscaTipoServidor" value="#{relatorioEquipeExtensao.buscaTipoServidor}" 
					onblur="javascript:verificarSelect(this,'checkBuscaTipoServidor','labelTipoServidor');">
						<f:selectItem itemLabel="-- SELECIONE UM TIPO DE SERVIDOR --" itemValue="-1"/>
		    	 		<f:selectItem itemLabel="DOCENTE" itemValue="1"/>
		    	 		<f:selectItem itemLabel="TÉCNICO ADMINISTRATIVO" itemValue="2"/>
		    	 		<f:selectItem itemLabel="CONSIGNATARIAS" itemValue="3"/>
		    	 		<f:selectItem itemLabel="TERCERIZADOS" itemValue="4"/>
		    	 		<f:selectItem itemLabel="BOLSISTAS" itemValue="5"/>
		    	 		<f:selectItem itemLabel="MÉDICO RESIDENTE" itemValue="6"/>
	 			 	</h:selectOneMenu>
					</td>
					<td><h:selectBooleanCheckbox value="#{relatorioEquipeExtensao.checkBuscaTipoServidor}" id="checkBuscaTipoServidor" style="display:none"/> </td>
					<h:inputHidden id="labelTipoServidor" value="#{relatorioEquipeExtensao.labelTipoServidor}" />
				</tr>

				<tr>
					<th class="obrigatorio">Função do Membro:</th>
					<td><h:selectOneMenu id="buscaFuncaoMembro" value="#{relatorioEquipeExtensao.buscaFuncaoMembro}" 
					onblur="javascript:verificarSelect(this,'checkBuscaFuncaoMembro');">
						<f:selectItem itemLabel="-- SELECIONE UMA FUNÇÃO DE MEMBRO --" itemValue="-1"/>
		    	 		<f:selectItems value="#{funcaoMembroEquipe.allCombo}" />
						<f:selectItem itemLabel="TODOS" itemValue="0"/>
		    	 	</h:selectOneMenu>
		    	 	</td>
					<td><h:selectBooleanCheckbox value="#{relatorioEquipeExtensao.checkBuscaFuncaoMembro}" id="checkBuscaFuncaoMembro" style="display:none"/> </td>
				</tr>

				<tr>
					<th class="obrigatorio">Ano de Referência:</th>
					<td colspan="2"><h:inputText value="#{relatorioEquipeExtensao.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton action="#{relatorioEquipeExtensao.gerarRelatorio}" value="Consultar" />
					<h:commandButton action="#{relatorioEquipeExtensao.cancelar}" value="Cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>

		</table>

	</h:form>

<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>