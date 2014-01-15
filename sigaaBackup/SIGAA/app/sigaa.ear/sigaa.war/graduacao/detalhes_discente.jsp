<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/rich" prefix="rich"%>
<%@ taglib uri="/tags/a4j" prefix="a4j"%>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="ctx" value="<%= request.getContextPath() %>"/>

<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>


${detalhesDiscenteBean.dadosDiscente}
<div class="detalhes-discente">
	<table class="dados-discente">
		<tr>
			<td rowspan="6" width="50px" style="text-align: center;"> 
				<c:if test="${detalhesDiscenteBean.obj.idFoto != null}">
					<img src="${ctx}/verFoto?idFoto=${detalhesDiscenteBean.obj.idFoto}&key=${ sf:generateArquivoKey(detalhesDiscenteBean.obj.idFoto) }" width="50" height="63"/>
				</c:if>
				<c:if test="${detalhesDiscenteBean.obj.idFoto == null}">
					<img src="${ctx}/img/no_picture.png" width="50" height="63"/>
				</c:if>
				<c:if test="${acesso.administradorSistema}">
					<p class="login"> (${detalhesDiscenteBean.usuario.login}) </p>
				</c:if>
			</td>
		</tr>

		<c:if test="${detalhesDiscenteBean.obj.graduacao}">
			<tr>
				<th style="text-align:right"> Ano/Período de Ingresso: </th>
				<td> ${detalhesDiscenteBean.obj.anoPeriodoIngresso} </td>
			</tr>
			<tr>
				<th style="text-align:right"> Forma de Ingresso: </th>
				<td> ${detalhesDiscenteBean.obj.formaIngresso.descricao} </td>
			</tr>
			<tr>
				<th width="25%" style="text-align:right"> Matriz Curricular: </th>
				<td> ${detalhesDiscenteBean.obj.matrizCurricular} </td>
			</tr>
		</c:if>

		<c:if test="${detalhesDiscenteBean.obj.stricto}">
			<tr>
				<th style="text-align:right"> Ano/Mês de Ingresso: </th>
				<td> ${detalhesDiscenteBean.obj.anoMesIngresso} </td>
			</tr>
			<tr>
				<th width="25%" style="text-align:right"> Orientador: </th>
				<td> ${detalhesDiscenteBean.obj.orientacao.nomeOrientador} </td>
			</tr>
			<tr>
				<th style="text-align:right"> Área de Concentração: </th>
				<td> ${detalhesDiscenteBean.obj.area} </td>
			</tr>										
			
			<tr>
				<th style="text-align:right"> CPF: </th>
				<td> ${detalhesDiscenteBean.obj.pessoa.cpfCnpjFormatado} </td>
			</tr>
			
			<tr>
				<th style="text-align:right"> Tel/Cel: </th>
				<td> ${detalhesDiscenteBean.obj.pessoa.telefone} <c:if test="${not empty detalhesDiscenteBean.obj.pessoa.telefone and not empty detalhesDiscenteBean.obj.pessoa.celular}">/</c:if> ${detalhesDiscenteBean.obj.pessoa.celular} </td>
			</tr>		
			
		</c:if>
		<c:if test="${detalhesDiscenteBean.obj == null}">
			<tr>
				<th> Não há registro de discente ativo para este aluno. </th>
			</tr>	
		</c:if>
	</table>
	
	<%--  Índices Acadêmicos --%>
	<c:if test="${not empty detalhesDiscenteBean.obj.discente.indices}">
		<table width="100%" class="dados-integralizacoes">
			<tr>
				<th class="cab" colspan="${fn:length(detalhesDiscenteBean.obj.discente.indices)}" style="text-align: center">Índices Acadêmicos</th>
			</tr>
			<tr>
				<c:forEach var="item" items="#{detalhesDiscenteBean.obj.discente.indices}">
					<th class="cab" width="${100 / fn:length(detalhesDiscenteBean.obj.discente.indices)}%">
						<acronym title="${item.indice.nome}">${item.indice.sigla}</acronym>
					</th>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach var="item" items="#{detalhesDiscenteBean.obj.discente.indices}">
					<td> ${item.valor} </td>
				</c:forEach>
			</tr>
		</table>
	</c:if>
	
	<c:if test="${detalhesDiscenteBean.obj.graduacao}">
	<table class="dados-integralizacoes">
		<tr>
			<th rowspan="3"> </th>
			<th colspan="3"> Obrigatórias </th>
			<th> Complementares </th>
			<th colspan="2" rowspan="2"> Total </th>
		</tr>
		<tr>
			<th colspan="2"> Comp. Curricular </th>
			<th> Atividade </th>
			<th> Comp. Curricular/Atividade </th>
		</tr>
		<tr>
			<th class="cab">CR</th>
			<th class="cab">CH</th>
			<th class="cab">CH</th>
			<th class="cab">CH</th>
			<th class="cab">CR</th>
			<th class="cab">CH</th>
		</tr>
		<tr>
			<th class="row"> Exigido </th>
			<td> ${detalhesDiscenteBean.obj.curriculo.crNaoAtividadeObrigatorio} </td>
			<td> ${detalhesDiscenteBean.obj.curriculo.chNaoAtividadeObrigatoria} </td>
			<td> ${detalhesDiscenteBean.obj.curriculo.chAtividadeObrigatoria} </td>
			<td> ${detalhesDiscenteBean.obj.curriculo.chOptativasMinima} </td>
			<td> ${detalhesDiscenteBean.obj.curriculo.crTotalMinimo} </td>
			<td> ${detalhesDiscenteBean.obj.curriculo.chTotalMinima} </td>
		</tr>
		<tr >
			<th class="row"> Integralizado </th>
			<td> ${detalhesDiscenteBean.obj.crNaoAtividadeObrigInteg} </td>
			<td> ${detalhesDiscenteBean.obj.chNaoAtividadeObrigInteg} </td>
			<td> ${detalhesDiscenteBean.obj.chAtividadeObrigInteg} </td>
			<td> ${detalhesDiscenteBean.obj.chOptativaIntegralizada} </td>
			<td> ${detalhesDiscenteBean.obj.crTotalIntegralizados} </td>
			<td> ${detalhesDiscenteBean.obj.chTotalIntegralizada} </td>
		</tr>
		<tr>
			<th class="row"> Pendente </th>
			<td> ${detalhesDiscenteBean.obj.crNaoAtividadeObrigPendente} </td>
			<td> ${detalhesDiscenteBean.obj.chNaoAtividadeObrigPendente} </td>
			<td> ${detalhesDiscenteBean.obj.chAtividadeObrigPendente} </td>
			<td> ${detalhesDiscenteBean.obj.chOptativaPendente} </td>
			<td> ${detalhesDiscenteBean.obj.crTotalPendentes} </td>
			<td> ${detalhesDiscenteBean.obj.chTotalPendente} </td>
		</tr>
	</table>
	</c:if>
	
	<c:if test="${detalhesDiscenteBean.obj.stricto}">
		<table class="dados-integralizacoes">
			<tr>
				<th class="row"> Créditos exigidos: </th>
				<td> ${detalhesDiscenteBean.obj.curriculo.crTotalMinimo} </td>
				<th class="row"> Créditos integralizados </th>
				<td> ${detalhesDiscenteBean.obj.crTotaisIntegralizados} </td>
				<th class="row"> Pendente </th>
				<td> ${detalhesDiscenteBean.obj.crTotalPendentes} </td>
			</tr>
		</table>
	</c:if>
	
	<br />

	<c:if test="${ detalhesDiscenteBean.proae }">
			<table width="100%" class="dados-integralizacoes">
				<tr>
					<th class="cab" colspan="3" style="text-align: center">Rendimento em ${ detalhesDiscenteBean.ano }.${ detalhesDiscenteBean.periodo }</th>
				</tr>
				<tr>
					<td style="text-align: left;" width="50%">Frequências nas turmas</td>
					<td><ufrn:format type="valor1" valor="${ detalhesDiscenteBean.frenquenciaTurma }"/>%</td>
				</tr>
				<tr>
					<td style="text-align: left;">Créditos aprovados em ${ detalhesDiscenteBean.ano }.${ detalhesDiscenteBean.periodo }</td>
					<td><ufrn:format type="valor1" valor="${ detalhesDiscenteBean.creditosAprovados }"/>%</td>
				</tr>
			</table>
	</c:if>

	<br />
		
	<c:if test="${ detalhesDiscenteBean.proae && not empty detalhesDiscenteBean.alunoDto }">
			<table width="100%" class="dados-integralizacoes">
				<tr>
					<th class="cab" colspan="3" style="text-align: center">Bolsas na Instituição</th>
				</tr>
				<tr>
					<th class="cab" style="text-align: left;">Descrição Bolsa</th>
					<th class="cab" style="text-align: center;">Início Bolsa</th>
					<th class="cab" style="text-align: center;">Termínio Bolsa</th>
				</tr>
				<c:forEach items="#{ detalhesDiscenteBean.alunoDto }" var="linha">
					<tr>
						<td style="text-align: left;">${ linha.curso.denominacao }</td>
						<td style="text-align: center;"><ufrn:format type="data" valor="${ linha.inicioBolsa }" /></td>
						<td style="text-align: center;"><ufrn:format type="data" valor="${ linha.fimBolsa }" /></td>
					</tr>
				</c:forEach>
			</table>
	</c:if>

</div>