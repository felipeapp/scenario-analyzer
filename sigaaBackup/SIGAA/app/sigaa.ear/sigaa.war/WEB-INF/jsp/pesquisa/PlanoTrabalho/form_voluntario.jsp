<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa"%>
<h2>
	<ufrn:steps />
</h2>

<html:form action="/pesquisa/cadastrarVoluntario" method="post">

	<table class="formulario" width="95%">
		<caption>Cadastro do Voluntário</caption>
	<tbody>
		<tr>
			<th width="20%"> Orientador: </th>
			<td>
				${membroProjetoDiscenteForm.obj.planoTrabalho.orientador.pessoa.nome}
			</td>
		</tr>
		<tr>
			<th class="required"> Projeto de Pesquisa: </th>
			<td>
				<html:select property="obj.planoTrabalho.projetoPesquisa.id" style="width: 95%">
					<html:options collection="projetos" property="id" labelProperty="codigoTitulo"/>
				</html:select>
			</td>
		</tr>
		<tr>
			<th class="required"> Bolsista: </th>
			<td>
                <c:set var="idAjax" value="obj.discente.id"/>
                <c:set var="nomeAjax" value="obj.discente.pessoa.nome"/>
                <c:set var="nivel" value="G"/>
                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
			</td>
		</tr>
		<tr>
			<th class="required"> Tipo da Bolsa: </th>
			<td>
				<html:select property="obj.planoTrabalho.tipoBolsa" style="width: 35%">
					<html:option value="<%= ""+TipoBolsaPesquisa.VOLUNTARIO %>">VOLUNTÁRIO</html:option>
					<html:option value="<%= ""+TipoBolsaPesquisa.BALCAO %>">BALCÃO</html:option>
				</html:select>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario" style="text-align: center"> Dados do Plano de Trabalho</td>
		</tr>
		<tr>
			<th class="required"> Título: </th>
			<td> <html:text property="obj.planoTrabalho.titulo" style="width:95%"/></td>
		</tr>
		<tr>
			<th class="required"> Objetivos: </th>
			<td> <html:textarea property="obj.planoTrabalho.objetivos" rows="8" style="width:95%"/></td>
		</tr>
		<tr>
			<th class="required"> Metodologia: </th>
			<td> <html:textarea property="obj.planoTrabalho.metodologia" rows="8" style="width:95%"/></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<html:button dispatch="cadastrar">Cadastrar</html:button>
				<html:button dispatch="cancelar">Cancelar</html:button>
			</td>
		</tr>
	</tfoot>
	</table>

</html:form>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>