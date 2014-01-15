<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>
<style>
.aviso {
	margin: 10px 20% 0;
	padding: 8px 10px;
	border: 1px solid #AAA;
	background: #EEE;
	color: #922;
	text-align: center;
	fon
}
.periodo {
	font-weight: bold;
	color: #292;
}

.fechado {
	color: #555;
}

.nivel {
	text-transform: uppercase;
}

#consulta {
	color: #292;
}

#consulta th,#consulta td {
	padding: 6px;
}

#consulta th {
	font-weight: bold;
}

tr.agrupador {
	background: #C4D2EB;
	font-weight: bold;
	padding-left: 20px;
}

tr.agrupador td {
	height: 10px;
}
</style>

<f:view>
	<h2>Vestibular</h2>

	<h:form id="form">
		<div class="descricaoOperacao">
		<p><b>Caro visitante,</b></p>
		<p>Nesta página você encontrará informações sobre o nosso Vestibular.</p>
		<p>Será possível visualizar as informações destes processos, como
		o curso a que ele se refere, o período de inscrição, alguns arquivos
		associados (como editais e manuais) e as instruções aos candidatos.</p>
		<p>Os períodos dos processos seletivos marcados na cor <b>verde</b>
		estão em aberto.</p>
		<p>Para se inscrever no Vestibular para os
		cursos de graduação ou para ter acesso à informações mais restritas, como o extrato de desempenho,
		será necessário realizar um login.</p>
		</div>

		<c:set var="processosSeletivos"	value="#{processoSeletivoVestibular.allInternos}" />

		<br />
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Inscrever-se no processo seletivo
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar dados do processo seletivo
		</div>

		<c:if test="${not empty processosSeletivos}">
			<br />
			<table class="listagem">
				<caption class="listagem">Últimos Vestibulares</caption>
				<thead>
					<tr>
						<th><b>Processo Seletivo</b></th>
						<th><b>Período de Inscrições</b></th>
						<th colspan="2"></th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{processosSeletivos}" var="item"	varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" } periodo ${!item.inscricoesCandidatoAbertas ? 'fechado' : ''} }">
						<td>${item.nome}</td>
						<td nowrap="nowrap">
							<c:if test="${not empty item.inicioInscricaoCandidato}">
								<ufrn:format type="data" valor="${item.inicioInscricaoCandidato}" /> a <ufrn:format	type="data" valor="${item.fimInscricaoCandidato}" />
							</c:if>
						</td>
						<td width="16">
							<c:choose>
								<c:when test="${item.inscricoesCandidatoAbertas}">
									<h:commandLink title="Inscrever-se dados do processo seletivo" action="#{processoSeletivoVestibular.viewPublico}" id="processoSeletivoVestibular_viewPublico">
										<h:graphicImage url="/img/seta.gif" alt="Inscrever-se dados do processo seletivo"/>
										<f:param name="id" value="#{item.id}" />
									</h:commandLink>
								</c:when>
								<c:otherwise>
									<h:commandLink title="Visualizar dados do processo seletivo" action="#{processoSeletivoVestibular.viewPublico}" id="processoSeletivoVestibular_viewPublico2">
										<h:graphicImage url="/img/view.gif" alt="Visualizar dados do processo seletivo"/>
										<f:param name="id" value="#{item.id}" />
									</h:commandLink>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:if>

		<c:if test="${empty processosSeletivos}">
			<div class="aviso">
			<p>Nenhum processo seletivo encontra-se aberto para inscrições
			neste nível de ensino.</p>
			</div>
		</c:if>

	</h:form>
</f:view>

<%@include file="/public/include/voltar.jsp"%>
<%@include file="/public/include/rodape.jsp"%>