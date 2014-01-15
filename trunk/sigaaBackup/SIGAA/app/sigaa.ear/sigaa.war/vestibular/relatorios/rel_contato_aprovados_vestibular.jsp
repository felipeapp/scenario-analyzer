<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	<h2>${relatoriosVestibular.nomeRelatorio }</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Processo Seletivo:</th>
		<td>${relatoriosVestibular.obj.nome}</td>
	</tr>
	<tr>
		<th>Convocação:</th>
		<td>
			<c:forEach items="#{relatoriosVestibular.convocacoesDiscente}" var="convocacao" end="0">
				${convocacao.convocacaoProcessoSeletivo.descricao}
			</c:forEach>
		</td>
	</tr>
</table>
</div>
<br/>

<c:forEach items="#{relatoriosVestibular.convocacoesDiscente}" var="linha" varStatus="status">
		<c:if test="${linha.discente.curso.id != _curso }">
			<c:set var="_curso" value="${linha.discente.curso.id}" />
			<c:if test="${status.index > 0}">
				</tbody>
				</table>
				<br/>
			</c:if>
			<table class="tabelaRelatorioBorda">
				<caption>${linha.discente.curso.nome} - ${linha.discente.matrizCurricular.turno.sigla} - ${linha.discente.curso.municipio.nome}</caption>
				<thead>
					<tr>
						<th style="text-align: center;">Matrícula</th>
						<th style="text-align: center;">Ingresso</th>
						<th style="text-align: left;" width="30%">Nome</th>
						<th style="text-align: left;" width="20%">Tipo de Convocação</th>
						<th style="text-align: left;" width="12%">Telefones</th>
						<th style="text-align: left;" width="10%">E-Mail</th>
						<th style="text-align: left;" width="30%">Endereço</th>
					</tr>
				</thead>
				<tbody>
		</c:if>
			<tr>
				<td style="text-align: center;">${linha.discente.matricula}</td>
				<td style="text-align: center;">${linha.discente.anoPeriodoIngresso}</td>
				<td>${linha.discente.discente.pessoa.nome}</td>
				<td>${linha.tipo.label}</td>
				<td style="text-align: left;" >
				<h:outputText value="(#{linha.discente.discente.pessoa.codigoAreaNacionalTelefoneFixo}) #{linha.discente.discente.pessoa.telefone}"
					rendered="#{not empty linha.discente.discente.pessoa.telefone}" /><br/>
				<h:outputText value="(#{linha.discente.discente.pessoa.codigoAreaNacionalTelefoneCelular}) #{linha.discente.discente.pessoa.celular}"
					rendered="#{not empty linha.discente.discente.pessoa.celular}" />
				</td>
				<td style="text-align: left;">${linha.discente.pessoa.email}</td>
				<td>${linha.discente.discente.pessoa.enderecoContato},<br/> 
					${linha.discente.discente.pessoa.enderecoContato.municipio.nome}/${linha.discente.discente.pessoa.enderecoContato.unidadeFederativa.sigla}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<br/>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>