<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<style>
	table.listagem tr.cota td {
		background-color: #C4D2EB;
		padding: 8px 10px 2px;
		border-bottom: 1px solid #BBB;
		font-variant: small-caps;

		font-style: italic;
	}

	#cotasDocente {
		background: #F3F3F3;
		margin: 0 100px 15px;
		padding: 5px;
	}

	#cotasDocente h3 {
		text-align: center;
		font-variant: small-caps;
		letter-spacing: 1px;
		border-bottom: 1px solid #AAA;
		margin: 8px 30px 3px;
	}

	#cotasDocente ul {
		margin: 10px;
	}

	#cotasDocente p {
		margin: 10px;
		font-style: italic;
		text-align: center;
	}

	#cotasDocente ul table{
		margin: 5px 20px 10px;
		font-variant: small-caps;
		width: 60%;
	}

	#cotasDocente ul table th{
		text-align: left;
	}

	#cotasDocente ul table td{
		text-align: right;
	}

	#cotasDocente ul table tr.separator th, #cotasDocente ul table tr.separator td{
		border-bottom: 1px solid #DDD;
	}
</style>

<h2>
	<ufrn:subSistema></ufrn:subSistema> &gt;
	Indicar/Substituir Bolsista
</h2>

<div class="descricaoOperacao">
	<p>
		<strong>Bem-vindo ao cadastro de bolsistas de pesquisa.</strong>
	</p>
	<br />
	<p>
		Para cada plano de trabalho listado você pode indicar alunos de graduação para atuarem como bolsistas ou voluntários.
		A indicação só pode ser realizada para planos de trabalho que não possuam discente definido.
	</p>
	<p>
		Caso deseje substituir um bolsista, é necessário primeiro finalizá-lo, antes de indicar um novo bolsista. Lembrando que 
		o novo interessado deve registrar previamente o seu interesse na bolsa e ainda ter aderido ao <strong>Cadastro Único de Bolsistas</strong>.
	</p>
</div>
<div class="infoAltRem">
	<html:img page="/img/pesquisa/indicar_bolsista.gif" style="overflow: visible;"/>: Indicar Bolsista
	<html:img page="/img/pesquisa/remover_bolsista.gif" style="overflow: visible;"/>: Finalizar Bolsista
	<html:img page="/img/email_go.png" style="overflow: visible;"/>: Enviar Mensagem aos Interessados
</div>

<c:if test="${not empty planos}">
	<table class="listagem" style="width: 100%;">
		<caption class="listagem">Planos de Trabalho Ativos</caption>
		<thead>
			<tr>
				<th style="text-align: center">Projeto</th>
				<th>Discente</th>
				<th style="text-align: center">Tipo de Bolsa</th>
				<th style="text-align: center">Período</th>
				<th style="text-align: center">Status</th>
				<th></th>
			</tr>
		</thead>

		<c:set var="ano"/>
		<c:forEach items="${planos}" var="plano" varStatus="i">

		<c:if test="${plano.projetoPesquisa.codigo.ano != ano}">
			<c:set var="ano" value="${ plano.projetoPesquisa.codigo.ano }" />
			<tr class="cota">
				<td colspan="7">
					Projetos de ${ano}
				</td>
			</tr>
		</c:if>

		<tr class="${i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			<td colspan="5">
				<i>
				<ufrn:format name="plano" property="titulo" type="texto" length="150"/>
				</i>
			</td>
			<td rowspan="2">
				<c:if test="${empty plano.membroProjetoDiscente.discente or plano.membroProjetoDiscente.inativo}">
					<a href="${ctx}/pesquisa/indicarBolsista.do?dispatch=enviarEmail&idPlanoTrabalho=${plano.id}">
						<img src="${ctx}/img/email_go.png"  border="0"  alt="Enviar Mensagem aos Interessados" title="Enviar Mensagem aos Interessados"/>
					</a>				
				
					<a href="${ctx}/pesquisa/indicarBolsista.do?dispatch=indicar&obj.id=${plano.id}">
						<img src="${ctx}/img/pesquisa/indicar_bolsista.gif" border="0" alt="Indicar Bolsista" title="Indicar Bolsista"/>
					</a>															
				</c:if>
				
				<c:if test="${not empty plano.membroProjetoDiscente.discente and not plano.membroProjetoDiscente.inativo}">													
					<a href="${ctx}/pesquisa/indicarBolsista.do?dispatch=remover&obj.id=${plano.id}">
						<img src="${ctx}/img/pesquisa/remover_bolsista.gif"  border="0"  alt="Finalizar Bolsista" title="Finalizar Bolsista"/>
					</a>												 
				</c:if>
			</td>
		</tr>

		<tr class="${i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			<td align="center" >
				${plano.projetoPesquisa.codigo}
			</td>
			<td>
				<c:choose>
					<c:when test="${empty plano.membroProjetoDiscente.discente or plano.membroProjetoDiscente.inativo}">
						<i>Não definido</i>
					</c:when>
					<c:otherwise>
						${plano.membroProjetoDiscente.discente.pessoa.nome}
					</c:otherwise>
				</c:choose>
			</td>
			<td align="center">
				${plano.tipoBolsaString}
			</td>
			<td align="center">
				<ufrn:format type="data" valor="${plano.dataInicio}"/> a 
				<ufrn:format type="data" valor="${plano.dataFim}"/> 
			</td>
			<td align="center"> ${plano.statusString} </td>
		</tr>
		 </c:forEach>

	</table>
</c:if>

<c:if test="${empty planos}">
	<div align="center"> <em> Não há planos de trabalho disponíveis para indicação! </em> </div>
</c:if>

<br />
<div id="cotasDocente">
	<c:if test="${not empty cotasDocente}">
	<h3> Cotas para o Docente </h3>
	<ul>
		<c:forEach var="cotaDocente" items="${ cotasDocente }">
		<li> <strong> Cota ${cotaDocente.edital.cota } <br />
				Edital: '${cotaDocente.edital.descricao }'
				</strong>
			<table>
				<tr>
					<th> Meu IPI </th>
					<td> ${cotaDocente.emissaoRelatorio.ipi}</td>
				</tr>
				<tr>
					<th> IPI médio do centro </th>
					<td> ${mediasDocente[cotaDocente.id]}</td>
				</tr>
				<tr>
					<th> Média dos meus projetos </th>
					<td> ${ cotaDocente.mediaProjetos } </td>
				</tr>
				<tr class="separator">
					<th> Meu FPPI </th>
					<td> ${ cotaDocente.fppi } </td>
				</tr>
				<c:forEach var="cota" items="${cotaDocente.cotas}">
					<tr>
						<th> Bolsas ${cota.tipoBolsa.descricaoResumida} concedidas </th>
						<td> <b>${ cota.quantidade }</b> </td>
					</tr>
				</c:forEach>
			</table>
		</li>
		</c:forEach>
	</ul>
	</c:if>

	<c:if test="${ empty cotasDocente }">
		<p> Você não foi contemplado com cotas de bolsa neste ano </p>
	</c:if>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>