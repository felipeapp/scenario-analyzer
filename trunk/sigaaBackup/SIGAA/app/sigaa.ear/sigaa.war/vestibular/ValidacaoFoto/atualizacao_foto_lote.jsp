<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h2><ufrn:subSistema /> > Alteração das Fotos dos Candidatos</h2>
	<center>
		<div class="descricaoOperacao">
			Utilize o nome da foto como sendo o número do cpf do candidato.
		</div>
	</center>
		<h:form enctype="multipart/form-data">
			<table class="formulario" width="60%">
				<caption>Alteração das Fotos dos Candidatos</caption>
					<tr>
						<th class="obrigatorio" style="padding-right: 15px;">Arquivo:</th>
						<td style="text-align: left;">
							<t:inputFileUpload value="#{validacaoFotoBean.arquivoUpload}" styleClass="file" immediate="false"
								id="nomeArquivo" onchange="$('fakeInput').value = this.value;"/>
						</td>
					</tr>
					<tfoot>
						<tr>
							<td colspan="3">
								<h:commandButton value="Adicionar" action="#{validacaoFotoBean.subAtualizacaoFotoLote}" id="adicionar"/> 
							</td>
						</tr>
					</tfoot>
			</table>
			
			<br /><br />
				<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
			<br /><br />


	<c:if test="${not empty validacaoFotoBean.pessoaVestibular}">
		<br/>
			<table class="formulario" border="1" width="100%">
				<caption>Resultados da Busca (${fn:length(validacaoFotoBean.pessoaVestibular)})</caption>
				<thead>
					<tr>
						<th style="text-align: center;">Atual Foto</th>
						<th style="text-align: center;">Dados do Candidato</th>
						<th style="text-align: center;">Nova Foto</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{validacaoFotoBean.pessoaVestibular}" var="item" varStatus="status" >
					<tr class="${status.index % 4 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: center;">
							<img src="${ctx}/verFoto?idArquivo=${item.idFoto}&key=${ sf:generateArquivoKey(item.idFoto) }" style="height: 150px"/>
						</td>
						<td style="text-align: right;">
							<table class="visualizacao" style="border: 0">
								<tr class="${status.index % 4 == 0 ? "linhaPar" : "linhaImpar" }">
									<th>CPF:</th>
									<td><h:outputText value="#{item.cpf_cnpjString}"/></td>
								</tr>
								<tr class="${status.index % 4 == 0 ? "linhaPar" : "linhaImpar" }">
									<th>Nome:</th>
									<td>
										<h:outputText value="#{item.nome}" />
									</td>
								</tr>
								<tr class="${status.index % 4 == 0 ? "linhaPar" : "linhaImpar" }">
									<th valign="top">Status Atual:</th>
									<td>
										<h:outputText value="#{item.statusFoto.descricao}" />	
									</td>
								</tr>
								<tr class="${status.index % 4 == 0 ? "linhaPar" : "linhaImpar" }">
									<th valign="top">Novo Status:</th>
									<td>
										<h:selectOneMenu id="novoStatusFoto" value="#{item.novoStatusFoto.id}">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
											<f:selectItems value="#{statusFotoMBean.allCombo}" />
										</h:selectOneMenu>
									</td>
								</tr>
							</table>
						</td>
						<td style="text-align: center;">
							<img src="${ctx}/verFotoCandidato?cpf=${item.cpf_cnpj}" style="height: 150px"/>
						</td>
				</tr>
				</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4">
							<h:commandButton value="Atualizar" action="#{validacaoFotoBean.atualizacaoFotoLote}" id="atualizarStatus" /> 
							<h:commandButton value="Cancelar" action="#{validacaoFotoBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>