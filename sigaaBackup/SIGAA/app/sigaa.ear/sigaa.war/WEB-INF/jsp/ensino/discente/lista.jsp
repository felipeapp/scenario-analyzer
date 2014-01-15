<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2 class="tituloPagina">
	<ufrn:subSistema /> &gt; Alunos
</h2>
<html:form action="/ensino/discente/wizard" method="get" focus="discente.matricula" styleId="form">
<html:hidden property="buscar" value="true" />
<table class="formulario" width="80%">
	<caption>Busca por Aluno</caption>
		<tr>
			<td width="90">
			<input type="radio" name="tipoBusca" value="1" class="noborder" id="buscaMatricula"/>
			<label for="buscaMatricula">Matrícula:</label>
			</td>
            <td><html:text property="discente.matricula" size="14" value=""
            		onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text></td>
        </tr>
        <tr>
        	<td width="90">
        	<input type="radio" name="tipoBusca" value="2" class="noborder" id="buscaNome">
	        <label for="buscaNome">Nome:</label>
        	</td>
        	<td><html:text property="discente.pessoa.nome" size="60" value="" onkeyup="CAPS(this)"
        	onfocus="javascript:forms[0].tipoBusca[1].checked = true;"></html:text></td>
        </tr>

		<ufrn:subSistema teste="not lato">
			<tr>
				<td width="90">
				<input type="radio" name="tipoBusca" value="3" class="noborder" id="buscaAno"/>
				<label for="buscaAno">Ano de Ingresso:</label>
				</td>
	            <td><html:text property="discente.anoIngresso" size="4" maxlength="4"
	            		onfocus="javascript:forms[0].tipoBusca[2].checked = true;"></html:text></td>
	        </tr>
	
			<tr>
				<td width="90">
				<input type="radio" name="tipoBusca" value="4" class="noborder" id="buscaTE"/>
				<label for="buscaTE">Turma de Entrada:</label>
				</td>
	            <td>
				<html:select property="discente.turmaEntradaTecnico.id" onfocus="marcaCheckBox('buscaTE')">
					<html:option value="-1">-- SELECIONE --</html:option>
					<html:options collection="turmasEntrada" property="id" labelProperty="descricao" />
				</html:select>
	            </td>
	        </tr>
        </ufrn:subSistema>
        
        <c:if test="${acesso.lato}">
			<tr>
				<td width="90">
				<input type="radio" name="tipoBusca" value="6" class="noborder" id="buscaCL"/>
				<label for="buscaCL">Curso:</label>
				</td>
	            <td>
				<html:select property="cursoLato.id" onfocus="marcaCheckBox('buscaCL')">
					<html:option value="">-- SELECIONE --</html:option>
					<html:options collection="cursosLato" property="id" labelProperty="nome" />
				</html:select>
	            </td>
	        </tr>
	    </c:if>
	    
        <c:if test="${acesso.coordenadorCursoLato || acesso.secretarioLato }">
	        <tr>
	        	<td width="90">
	        	<input type="radio" name="tipoBusca" value="5" class="noborder" id="buscaTodos">
		        <label for="buscaTodos">Todos:</label>
	        	</td>
	        	<td> </td>
	        </tr>
	    </c:if>
	    <tfoot>
	        <tr>
	        	<td colspan="2" align="center">
	        	<html:button  value="Consultar" dispatch="list" />
	        	<html:button  value="Cancelar" dispatch="cancelar" cancelar="true"/>
	        	</td>
	        </tr>
	    </tfoot>
     </table>
</html:form>

	<c:if test="${not empty lista}">
	<br>
	<div class="infoAltRem">
	<ufrn:checkRole papeis="<%=new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.GESTOR_LATO,SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_LATO,SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR}%>">
		<html:img page="/img/alterar.gif" style="overflow: visible;" title="Atualizar Dados Pessoais"/>: Atualizar Dados Pessoais
		<html:img page="/img/user.png" style="overflow: visible;" title="Atualizar Dados Acadêmicos"/>: Atualizar Dados Acadêmicos
	</ufrn:checkRole>
	<ufrn:checkRole papeis="<%=new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.GESTOR_LATO,SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR}%>">
		<html:img page="/img/delete.gif" style="overflow: visible;" title="Remover Discente"/>: Remover Discente
	</ufrn:checkRole>
	</div>
	</c:if>

	<br>
	<c:if test="${empty forward}">
		<ufrn:table  collection="${lista}" 
			properties="matricula,pessoa.nome,curso.descricao,anoEntrada,statusString"
			headers="Matrícula, Nome, Curso, Ano, Status"
			title="Alunos Encontrados (${fn:length(lista)})" pageSize="20"
			links="src='${ctx}/img/alterar.gif',${ctx}/ensino/discente/wizard.do?pessoaId={pessoa.id}&proximoId={id}&dispatch=redirectDadosPessoais&nextView=dadosPessoais,Atualizar Dados Pessoais;
					src='${ctx}/img/user.png',${ctx}/ensino/discente/wizard.do?pessoaId={pessoa.id}&proximoId={id}&dispatch=redirectDadosDiscente&nextView=dadosDiscente,Atualizar Dados Acadêmicos;
				   src='${ctx}/img/delete.gif',?discenteId={id}&dispatch=remove,Remover Discente"
			linksRoles="<%=new int[][] {
				   new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.GESTOR_LATO,SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_LATO,SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR},
				   new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.GESTOR_LATO,SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_LATO,SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR},
						   new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.GESTOR_LATO,SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR}}%>"/>
	</c:if>
	<c:if test="${not empty sessionScope.forward}">
		<c:if test="${not empty lista}">
		<div class="infoAltRem">
		    <html:img page="/img/seta.gif" style="overflow: visible;"/>: Selecionar este Aluno
		</div>
		</c:if>
		<ufrn:table collection="${lista}"
			properties="matricula,pessoa.nome, curso.descricao"
			headers="Matrícula, Nome, Curso"
			title="Alunos" crud="false"
			links="src='${ctx}/img/seta.gif',/sigaa/ensino/ver${forward}.do?id={id}&dispatch=carregar${forward}"
			linksRoles="<%= new int[][] {new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.GESTOR_LATO,SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_LATO,SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR}}%>"/>
	</c:if>


<br><br>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>