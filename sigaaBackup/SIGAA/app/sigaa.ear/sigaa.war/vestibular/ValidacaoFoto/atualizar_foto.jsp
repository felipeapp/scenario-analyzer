<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<h2><ufrn:subSistema /> > Atualizar Foto</h2>
	
<h:form id="formStatus" enctype="multipart/form-data">
<br/>
	<table class="formulario" border="1" width="100%">
		<caption>Candidato</caption>
		<thead>
			<tr>
				<th style="text-align: center;">Foto</th>
				<th>Dados do Candidato</th>
			</tr>
		</thead>
		<tbody>

		<td style="text-align: center;">
			<img src="${ctx}/verFoto?idArquivo=${validacaoFotoBean.obj.idFoto}&key=${ sf:generateArquivoKey(validacaoFotoBean.obj.idFoto) }" style="height: 120px"/>
		</td>
		<td style="text-align: right;">
			<table class="visualizacao" style="border: 0">
				<tr class="${status.index % 4 < 2 ? "linhaPar" : "linhaImpar" }">
					<th>CPF:</th>
					<td style="text-align: left;"><h:outputText value="#{validacaoFotoBean.obj.cpf_cnpjString}"/></td>
				</tr>
				<tr class="${status.index % 4 < 2 ? "linhaPar" : "linhaImpar" }">
					<th>Nome:</th>
					<td style="text-align: left;">
						<h:outputText value="#{validacaoFotoBean.obj.nome}" />
					</td>
				</tr>
				<tr class="${status.index % 4 < 2 ? "linhaPar" : "linhaImpar" }">
					<th valign="top">Status Atual:</th>
					<td style="text-align: left;">
						<h:outputText value="#{validacaoFotoBean.obj.statusFoto.descricao}" />	
					</td>
				</tr>
				<tr>
					<th valign="top">Novo Status:</th>
					<td style="text-align: left;">
						<h:selectOneMenu id="novoStatusFoto" value="#{validacaoFotoBean.obj.novoStatusFoto.id}">
							<f:selectItems value="#{statusFotoMBean.allCombo}" />
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio" style="padding-right: 15px;">Arquivo:</th>
					<td style="text-align: left;">
						<t:inputFileUpload value="#{validacaoFotoBean.arquivoUpload}" styleClass="file" immediate="false"
							id="nomeArquivo" onchange="$('fakeInput').value = this.value;"/>
					</td>
				</tr>
			</table>
		</td>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="<< Voltar" action="#{validacaoFotoBean.carregarListagem}" id="voltar" />
					<h:commandButton value="Atualizar" action="#{validacaoFotoBean.atualizarFoto}" id="atualizar" /> 
					<h:commandButton value="Cancelar" action="#{validacaoFotoBean.cancelar}" onclick="#{confirm}" 
						immediate="true" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br />
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>