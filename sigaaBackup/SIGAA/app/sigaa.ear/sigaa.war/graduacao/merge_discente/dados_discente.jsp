<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/rich" prefix="rich"%>
<%@ taglib uri="/tags/a4j" prefix="a4j"%>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="ctx" value="<%= request.getContextPath() %>"/>


<c:set var="dadosDiscente" value="${mergeDadosDiscenteMBean.dadosDiscente}" />
	<table class="listagem" style="width: 100%">
		<caption>Dados do Discente</caption>
		<tbody>
		<tr>
			<td rowspan="5" width="50px" style="text-align: center;"> 
				<c:if test="${dadosDiscente.idFoto != null}">
					<img src="${ctx}/verFoto?idFoto=${dadosDiscente.idFoto}&key=${ sf:generateArquivoKey(dadosDiscente.idFoto) }" width="50" height="63"/>
				</c:if>
				<c:if test="${dadosDiscente.idFoto == null}">
					<img src="${ctx}/img/no_picture.png" width="50" height="63"/>
				</c:if>
				<c:if test="${acesso.administradorSistema}">
					<p class="login">(${dadosDiscente.usuario.login})</p>
				</c:if>
			</td>
		</tr>

		<c:if test="${dadosDiscente.graduacao}">
			<tr>
				<th style="text-align:right; vertical-align: top" class="rotulo"> Ano/Período de Ingresso: </th>
				<td> ${dadosDiscente.anoPeriodoIngresso} </td>
				<th style="text-align:right; vertical-align: top" class="rotulo"> Forma de Ingresso: </th>
				<td colspan="3"> ${dadosDiscente.formaIngresso.descricao} </td>
			</tr>
			<tr>
				<th style="text-align:right; vertical-align: top" class="rotulo"> Matriz Curricular: </th>
				<td colspan="5"> ${dadosDiscente.matrizCurricular} </td>
			</tr>
		</c:if>

		<c:if test="${dadosDiscente.stricto}">
			<tr>
				<th style="text-align:right; vertical-align: top" class="rotulo"> Ano de Ingresso: </th>
				<td> ${dadosDiscente.anoIngresso}</td>
				<th style="text-align:right; vertical-align: top" class="rotulo"> Orientador: </th>
				<td colspan="3"> ${dadosDiscente.orientacao.nomeOrientador} </td>
			</tr>
			<tr>
				<th style="text-align:right; vertical-align: top" class="rotulo"> Área de Concentração: </th>
				<td colspan="5"> ${dadosDiscente.area} </td>
			</tr>										
		</c:if>
		<tr>
			<th style="text-align:right; vertical-align: top" class="rotulo"> Identidade: </th>
			<td> ${dadosDiscente.pessoa.identidade} </td>
			<th style="text-align:right; vertical-align: top" class="rotulo"> CPF: </th>
			<td> ${dadosDiscente.pessoa.cpfCnpjFormatado} </td>
			<th style="text-align:right vertical-align: top" class="rotulo"> Data Nasc.: </th>
			<td> ${ dadosDiscente.pessoa.celular} </td>
		</tr>		
		<tr>
			<th width="12%" class="rotulo">Nome da Mãe:</th>
			<td width="20%"> ${dadosDiscente.pessoa.nomeMae} </td>
			<th width="12%" class="rotulo">Nome do Pai:</th>
			<td width="20%"> ${dadosDiscente.pessoa.nomePai} </td>
			<th width="12%"></th>
			<td width="20%"></td>
		</tr>
		</tbody>
	</table>
	
</div>