<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Justificativa de Ausência</h2>

	<div class="descricaoOperacao">Justifique a ausência na
	reunião, ou na aplicação, na caixa de texto abaixo. Opcionalmente,
	inclua um arquivo que comprove a justificativa.</div>

	<h:form id="form" enctype="multipart/form-data">
	<a4j:keepAlive beanName="justificativaAusencia" />
	<c:set var="item" value="${justificativaAusencia.obj.fiscal}"/>
	<table class="visualizacao" style="width: 85%">
		<caption>Dados do Fiscal</caption>
		<tbody>
			<c:if test="${item.perfilDiscente}">
				<tr>
					<th>Matrícula:</th>
					<td>${item.discente.matricula}</td>
				</tr>
				<tr>
					<th>Nome:</th>
					<td>${item.discente.nome}</td>
				</tr>
				<tr>
					<th>Curso:</th>
					<td>${item.discente.curso.descricaoCompleta}</td>
				</tr>
			</c:if>
			<c:if test="${item.perfilServidor}">
				<tr>
					<th>SIAPE:</th>
					<td>${item.servidor.siape}</td>
				</tr>
				<tr>
					<th>Nome:</th>
					<td>${item.servidor.nome}</td>
				</tr>
				<tr>
					<th>Unidade:</th>
					<td>${item.servidor.unidade.nome}</td>
				</tr>
			</c:if>
			<tr>
				<th>Processo Seletivo:</th>
				<td>${item.processoSeletivoVestibular.nome}</td>
			</tr>
		</tbody>
	</table>
	<br/>
	<c:if test="${justificativaAusencia.obj.id == 0}">
		<table class="formulario" width="85%">
			<caption>Informe Abaixo, o Motivo da Ausência</caption>
			<tr>
				<th class="required">Justificativa da Ausência:</th>
				<td>
					<h:inputTextarea value="#{justificativaAusencia.obj.justificativa}" id="observacao" rows="4" cols="60" 
			  			onkeyup="if (this.value.length > 1000) this.value = this.value.substring(0, 1000); $('form:observacao_count').value = 1000 - this.value.length;" 
						onkeydown="if (this.value.length > 1000) this.value = this.value.substring(0, 1000); $('form:observacao_count').value = 1000 - this.value.length;">
					</h:inputTextarea>
					<br/>
					Você pode digitar <h:inputText id="observacao_count" size="3" value="#{1000 - fn:length(buscaRegistroDiploma.observacao.observacao)}" disabled="true" /> caracteres.
				</td>
			</tr>
			<tr>
				<th>Arquivo:</th>
				<td>
					<t:inputFileUpload value="#{justificativaAusencia.arquivo}" style="width:95%;"/> 
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden	value="#{justificativaAusencia.obj.id}" /> 
						<h:commandButton value="#{justificativaAusencia.confirmButton}"	action="#{justificativaAusencia.cadastrar}" />
						<c:if test="${acesso.vestibular}">
							<h:commandButton value="<< Escolher outro Fiscal" action="#{justificativaAusencia.listarFiscaisAusentes}" />
						</c:if> 
						<h:commandButton value="Cancelar" action="#{justificativaAusencia.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</c:if>
	<c:if test="${justificativaAusencia.obj.id != 0}">
		<table class="visualizacao">
			<caption>Segue Abaixo, a Justificativa da Ausência</caption>
			<tr>
				<th valign="top" width="25%">Justificativa da Ausência:</th>
				<td>
					<h:outputText value="#{justificativaAusencia.obj.justificativa}"/>
				</td>	
			</tr>
			<tr>
				<th>Arquivo:</th>
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
				<th>Situação:</th>
				<td>
					${ justificativaAusencia.obj.descricaoStatus } 
				</td>
			</tr>
			<c:if test="${ justificativaAusencia.obj.indeferido}">
				<tr>
					<th>Motivo do Indeferimento:</th>
					<td>
						${ justificativaAusencia.obj.motivoIndeferimento } 
					</td>
				</tr>
			</c:if>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
