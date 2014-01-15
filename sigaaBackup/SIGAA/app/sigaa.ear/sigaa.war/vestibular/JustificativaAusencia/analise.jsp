<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Justificativa de Ausência</h2>

	<div class="descricaoOperacao">Analise a justificativa dada pelo fiscal
	e selecione um status de acordo com sua avaliação. Caso a justificativa
	seja indeferida, deve-se informar o motivo do indeferimento.</div>

	<h:form id="form" enctype="multipart/form-data">
		<a4j:keepAlive beanName="justificativaAusencia" />
		<table class="formulario">
			<caption>Dados da Justificativa</caption>
			<tbody>
			<tr>
				<th><b>Processo Seletivo:</b></th>
				<td>${justificativaAusencia.obj.fiscal.processoSeletivoVestibular.nome}</td>
			</tr>
			<c:if test="${justificativaAusencia.obj.fiscal.perfilDiscente}">
				<tr>
					<th><b>Fiscal:</b></th>
					<td>${justificativaAusencia.obj.fiscal.discente.matriculaNome}</td>
				</tr>
				<tr>
					<th><b>Curso:</b></th>
					<td>${justificativaAusencia.obj.fiscal.discente.curso.descricaoCompleta}</td>
				</tr>
			</c:if>
			<c:if test="${justificativaAusencia.obj.fiscal.perfilServidor}">
				<tr>
					<th><b>Fiscal:</b></th>
					<td>${justificativaAusencia.obj.fiscal.servidor.siapeNome}</td>
				</tr>
				<tr>
					<th><b>Unidade:</b></th>
					<td>${justificativaAusencia.obj.fiscal.servidor.unidade.nome}</td>
				</tr>
			</c:if>
			<tr>
				<th valign="top" width="25%"><b>Justificativa da Ausência:</b></th>
				<td>
					<h:outputText value="#{justificativaAusencia.obj.justificativa}"/>
				</td>	
			</tr>
			<tr>
				<th><b>Arquivo:</b></th>
				<td>
					<c:if test="${not empty justificativaAusencia.obj.idArquivo}">
						<a href="${ctx}/verProducao?idProducao=${ justificativaAusencia.obj.idArquivo}&key=${ sf:generateArquivoKey(justificativaAusencia.obj.idArquivo) }"
						target="_blank"><h:graphicImage value="/img/icones/document_view.png" style="overflow: visible;" /></a>
					</c:if> 
					<c:if test="${empty justificativaAusencia.obj.idArquivo}">
						Não foi enviado arquivo no momento da justificativa.
					</c:if>
				</td>
			</tr>
			<tr>
				<th class="required">Situação:</th>
				<td>
					<h:selectOneMenu value="#{justificativaAusencia.obj.status}">
						<a4j:support event="onchange" reRender="form"/>
						<f:selectItems value="#{justificativaAusencia.allStatusCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${ justificativaAusencia.obj.indeferido }">
				<tr>
					<th>Motivo do Indeferimento:</th>
					<td>
						<h:inputTextarea value="#{ justificativaAusencia.obj.motivoIndeferimento }" id="motivoIndeferimento" rows="4" cols="60" 
				  			onkeyup="if (this.value.length > 1000) this.value = this.value.substring(0, 1000); $('form:motivoIndeferimento_count').value = 1000 - this.value.length;" 
							onkeydown="if (this.value.length > 1000) this.value = this.value.substring(0, 1000); $('form:motivoIndeferimento_count').value = 1000 - this.value.length;">
						</h:inputTextarea>
						<br/>
						Você pode digitar <h:inputText id="motivoIndeferimento_count" size="3" value="#{1000 - fn:length(justificativaAusencia.obj.motivoIndeferimento)}" disabled="true" /> caracteres.
						</div>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Observações:<br/>(Não visualizadas pelo fiscal)</th>
				<td>
					<h:inputTextarea value="#{ justificativaAusencia.obj.observacoes }" id="observacoes" rows="4" cols="60" 
			  			onkeyup="if (this.value.length > 1000) this.value = this.value.substring(0, 1000); $('form:observacoes_count').value = 1000 - this.value.length;" 
						onkeydown="if (this.value.length > 1000) this.value = this.value.substring(0, 1000); $('form:observacoes_count').value = 1000 - this.value.length;">
					</h:inputTextarea>
					<br/>
					Você pode digitar <h:inputText id="observacoes_count" size="3" value="#{1000 - fn:length(justificativaAusencia.obj.observacoes)}" disabled="true" /> caracteres.
					</div>
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden	value="#{justificativaAusencia.obj.id}" />
						<h:inputHidden	value="#{justificativaAusencia.idProcessoSeletivo}" />  
						<h:commandButton value="Alterar"  action="#{justificativaAusencia.cadastrar}" /> 
						<h:commandButton value="Cancelar" action="#{justificativaAusencia.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
