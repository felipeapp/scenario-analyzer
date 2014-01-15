<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Detalhes do Fiscal</h2>
	<br/>
	
	<table class="visualizacao" width="50%">
		<caption>Dados do Fiscal</caption>
		<c:if test="${not empty fiscal.obj.discente}">
			<tr>
				<th width="25%">Categoria:</th>
				<td>Discente</td>
			</tr>
			<tr>
				<th>Matrícula</th>
				<td>${fiscal.obj.discente.matricula}</td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td>${fiscal.obj.discente.nome}</td>
			</tr>
			<tr>
				<th>Curso</th>
				<td>${fiscal.obj.discente.curso}</td>
			</tr>
		</c:if>
		<c:if test="${not empty fiscal.obj.servidor}">
			<tr>
				<th>Categoria:</th>
				<td>Servidor</td>
			</tr>
			<tr>
				<th>SIAPE:</th>
				<td>${fiscal.obj.servidor.siape}</td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td>${fiscal.obj.servidor.nome}</td>
			</tr>
			<tr>
				<th>Unidade:</th>
				<td>${fiscal.obj.servidor.unidade}</td>
			</tr>
		</c:if>
		<tr>
			<th>Processo Seletivo:</th>
			<td>${fiscal.obj.processoSeletivoVestibular.nome}</td>
		</tr>
		<tr>
			<th>Titularidade:</th>
			<td>
				<c:if test="${fiscal.obj.reserva}">Reserva</c:if> 
				<c:if test="${not fiscal.obj.reserva}">Titular</c:if>
			</td>
		</tr>
		<tr>
			<th>Experiência:</th>
			<td>
				<c:if test="${fiscal.obj.novato}">Novato</c:if> 
				<c:if test="${not fiscal.obj.novato}">Experiente</c:if>
			</td>
		</tr>
		<tr>
			<th>Cadastro/Recadastro:</th>
			<td>
				<c:if test="${fiscal.obj.recadastro}">Recadastro</c:if> 
				<c:if test="${not fiscal.obj.recadastro}">Cadastro</c:if>
			</td>
		</tr>
		<tr>
			<th>Local de Aplicação de Prova:</th>
			<td>
				${fiscal.obj.localAplicacaoProva.nome}
			</td>
		</tr>
		<tr>
			<th>Conceito:</th>
			<td>
				${fiscal.obj.descricaoConceito}
			</td>
		</tr>
		<tr>
			<th>Frequência:</th>
			<td>
				${fiscal.obj.frequencia} dias
			</td>
		</tr>
		<tr>
			<th>Presente à Reunião:</th>
			<td>
				<ufrn:format type="simnao" valor="${fiscal.obj.presenteReuniao}"></ufrn:format>
			</td>
		</tr>
		<tr>
			<th>Presente à Aplicação:</th>
			<td>
				<ufrn:format type="simnao" valor="${fiscal.obj.presenteAplicacao}"></ufrn:format>
			</td>
		</tr>
		<c:if test="${!fiscal.obj.presenteAplicacao || !fiscal.obj.presenteReuniao}">
			<tr>
				<td class="subFormulario" colspan="2">Justificativa de Ausência</td>
			</tr>
			<c:if test="${ not empty fiscal.obj.justificou }" >
				<tr>
					<th>Justificou:</th>
					<td>
						<ufrn:format type="simnao" valor="${fiscal.obj.justificou}"></ufrn:format>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty fiscal.justificativa}">
				<tr>
					<th valign="top">Justificativa Dada:</th>
					<td>
						${fiscal.justificativa.justificativa}
					</td>
				</tr>
				<tr>
					<th>Arquivo Enviado:</th>
					<td>
						<c:if test="${not empty fiscal.justificativa.idArquivo}">
							<a href="${ctx}/verProducao?idProducao=${ fiscal.justificativa.idArquivo}&key=${ sf:generateArquivoKey(fiscal.justificativa.idArquivo) }"
							target="_blank"><h:graphicImage value="/img/icones/document_view.png" style="overflow: visible;" /></a>
						</c:if> 
						<c:if test="${empty fiscal.justificativa.idArquivo}">
							Não foi enviado arquivo no momento da justificativa.
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Status:</th>
					<td>
						${fiscal.justificativa.descricaoStatus}
					</td>
				</tr>
				<tr>
					<th>Motivo do Indeferimento:</th>
					<td>
						${fiscal.justificativa.motivoIndeferimento}
					</td>
				</tr>
				<tr>
					<th>Observação Anotada:</th>
					<td>
						${fiscal.justificativa.observacoes}
					</td>
				</tr>
			</c:if>
			<c:if test="${empty fiscal.justificativa}">
				<tr>
					<th>Justificativa:</th>
					<td>O fiscal não cadastrou a justificativa de ausência</td>
				</tr>
			</c:if>
		</c:if>
		<tr>
			<td class="subFormulario" colspan="2">Dados da Inscrição</td>
		</tr>
		<tr>
			<th>Número de Inscrição:</th>
			<td>${fiscal.obj.inscricaoFiscal.numeroInscricao}</td>
		</tr>
		<tr>
			<th valign="top">Locais de Aplicação Optados na Inscrição:</th>
			<td>
					<c:forEach var="item" items="#{fiscal.obj.inscricaoFiscal.localAplicacaoProvas}" varStatus="status">
						${status.index + 1} - ${item.nome}<br/>
					</c:forEach>
				</ol>
			</td>
		</tr>
		<tr>
			<th>Disponibilidade para Viajar:</th>
			<td><ufrn:format type="simnao" valor="${fiscal.obj.inscricaoFiscal.disponibilidadeOutrasCidades}"></ufrn:format></td>
		</tr>
	</table>
	<div align="center"><a href="javascript: history.go(-1);"><< Voltar </a></div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>