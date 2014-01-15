<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2 class="tituloPagina">
	<ufrn:subSistema /> &gt; Alunos
</h2>
<f:view>
<h:form id="formBuscaDiscenteTecnico" >
<html:hidden property="buscar" value="true" />
<table class="formulario" width="100%">
	<caption>Busca por Aluno</caption>
		<tr>
			<td>
			<input type="radio" name="tipoBusca" value="1" class="noborder" id="buscaMatricula"/>
			<h:selectBooleanCheckbox value="#{discenteTecnico.buscaMatricula}" styleClass="noborder" id="checkMatricula" />
			<label for="buscaMatricula">Matrícula</label>
			</td>
            <td><h:inputText value="#{discenteTecnico.cpMatricula}" id="cpMatricula" size="14" 
            		onfocus="javascript:forms[0].tipoBusca[0].checked = true;"/></td>
        </tr>
        <tr>
        	<td>
        	<input type="radio" name="tipoBusca" value="2" class="noborder" id="buscaNome">
	        <label for="buscaNome">Nome</label>
        	</td>
        	<td><h:inputText value="#{discenteTecnico.cpNomeDiscente}" id="cpNomeDiscente" size="60" onkeyup="CAPS(this)"
        	onfocus="javascript:forms[0].tipoBusca[1].checked = true;"/></td>
        </tr>

		<ufrn:subSistema teste="not lato">
		<tr>
			<td>
			<input type="radio" name="tipoBusca" value="3" class="noborder" id="buscaAno"/>
			<label for="buscaAno">Ano de Ingresso</label>
			</td>
            <td><h:inputText value="#{discenteTecnico.cpAnoIngresso}" id="cpAnoIngresso" size="4" maxlength="4"
            		onfocus="javascript:forms[0].tipoBusca[2].checked = true;"/></td>
        </tr>

		<tr>
			<td>
			<input type="radio" name="tipoBusca" value="4" class="noborder" id="buscaTE"/>
			<label for="buscaTE">Turma de Entrada</label>
			</td>
            <td>
			<h:selectOneMenu value="#{discenteTecnico.cpTurma}" id="cpTurma" onfocus="marcaCheckBox('buscaTE')">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue=""/>
				<f:selectItems value="#{discenteTecnico.turmasCombo}"/>
			</h:selectOneMenu>
            </td>
        </tr>
        </ufrn:subSistema>
        
         <tfoot>
	        <tr>
	        	<td colspan="2" align="center">
	        	<h:commandButton  value="Buscar" id="Buscar" action="#{discenteTecnico.buscar}"/>
	        	<h:commandButton  value="Cancelar" id="Cancelar" action="#{discenteTecnico.cancelar}"/>
	        	</td>
	        </tr>
	    </tfoot>
     </table>
</h:form>

<c:if test="${not empty discenteTecnico.resultadoBusca}">
	<br>
	<div class="infoAltRem">
	<ufrn:checkRole papeis="<%=new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.GESTOR_LATO,SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_LATO}%>">
		<html:img page="/img/alterar.gif" style="overflow: visible;"/>
		 : Atualizar Dados Pessoais
		<html:img page="/img/user.png" style="overflow: visible;"/>
		 : Atualizar Dados Acadêmicos
	</ufrn:checkRole>
	<ufrn:checkRole papeis="<%=new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.GESTOR_LATO}%>">
		<html:img page="/img/delete.gif" style="overflow: visible;"/>
		 : Remover Discente
	</ufrn:checkRole>
	</div>
	
	
	<table class="listagem">
			<thead>
				<tr>
					<th>Matrícula</th>
					<th>Nome</th>
					<th>Curso</th>
					<th>Ano</th>
					<th>Status</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="_discente" items="${discenteTecnico.resultadoBusca}">
				<tr>
					<td>${_discente.matricula}</td>	
					<td>${_discente.pessoa.nome}</td>
					<td>${_discente.curso.descricao}</td>
					<td>${_discente.anoEntrada}</td>
					<td>${_discente.statusString}</td>
					
					<td>
						<ufrn:checkRole papeis="<%=new int[] {SigaaPapeis.GESTOR_LATO,SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_LATO}%>">
							<h:commandLink id="atualizarDiscenteLato" action="#{discenteLato.atualizar}">
								<h:graphicImage value="#{ctx}/img/alterar.gif" />
							</h:commandLink>
						</ufrn:checkRole>
						<ufrn:checkRole papeis="<%=new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.COORDENADOR_TECNICO}%>">
						<h:commandLink id="atualizarDiscenteTecnicos" action="#{discenteTecnico.atualizar}">
							<h:graphicImage value="#{ctx}/img/alterar.gif" />
						</h:commandLink>
						</ufrn:checkRole>
					</td>
				
					<ufrn:checkRole papeis="<%=new int[] {SigaaPapeis.GESTOR_LATO,SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_LATO}%>">
					<td>
						<h:commandLink id="alterarDadosDiscenteLato" action="#{ alteracaoDadosDiscente.iniciar}">
							<h:graphicImage value="#{ctx}/img/user.png" />
						</h:commandLink>
					</td>
					</ufrn:checkRole>
					
					<ufrn:checkRole papeis="<%=new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.COORDENADOR_TECNICO}%>">
					<td>
						<a href="?discenteId=${_discente.statusString}&dispatch=remove" id="alterarDadosDiscenteTecnico">
							<h:graphicImage value="#{ctx}/img/delete.gif" />
						</a>
					</td>
					</ufrn:checkRole>
				</tr>
				</c:forEach>
			</tbody>				   
	   </table>
	
</c:if>
	

</f:view>
<br><br>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>