<%@page import="br.ufrn.sigaa.jsf.OperacaoDadosPessoais"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Docentes Externos</h2>
	<h:outputText value="#{docenteExterno.create}" />
	
	<h:form id="formBusca">
		<table class="formulario" width="51%">
			<caption>Busca de Docentes Externos</caption>
			<tr>		
				<th>Nome:</th>
				<td><h:inputText value="#{docenteExterno.obj.pessoa.nome}" size="60"
					maxlength="60" onkeyup="CAPS(this)" id="txtNome"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{docenteExterno.buscar}" id="btnBuscar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{usuarioDocenteExterno.cancelar}" id="btCancelar"/>
					</td>
					
				</tr>

			</tfoot>
		</table>
	
	<br>
	
	<c:if test="${not empty docenteExterno.docentesEncontrados}">
		<div class="infoAltRem">
			<h:graphicImage value="/img/user_edit.png"style="overflow: visible;"/>: Alterar Dados Pessoais
			<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>:	Cadastrar Usuário <br/>
		</div>
		<table class="listagem">
			<caption>Docentes Encontrados (${ fn:length(docenteExterno.docentesEncontrados) })</caption>
			
			<thead>
				<td>Matrícula</td>
				<td>Instituição</td>
				<td>Nome</td>
				<td>Unidade</td>
				<td colspan="4"></td>
			</thead>
			<tbody>
			<c:forEach items="#{docenteExterno.docentesEncontrados}" var="docente" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${docente.matricula}</td>
					<td>${docente.instituicao.sigla}</td>
					<td>${docente.nome}</td>
					<td>${docente.unidade}</td>
					<td width="10px">
							<h:commandLink   styleClass="noborder" id="btaonAlterarPessoa" immediate="true"
								action="#{dadosPessoais.atualizar}">
								<f:param name="id" value="#{docente.pessoa.id}"/>
								<f:param id="codigoOperacaoAlterarDadosPessoais" name="codigoOperacao" value="#{dadosPessoais.codigoOperacao}" />
								<h:graphicImage url="/img/user_edit.png" title="Alterar Dados Pessoais" />								
							</h:commandLink>
					</td>
					<td width="10px">
							<h:commandLink  styleClass="noborder" id="btnatualizar" immediate="true"
								action="#{docenteExterno.atualizar}">
								<f:param name="id" value="#{docente.id}"/>
								<h:graphicImage url="/img/alterar.gif" title="Alterar"/>
							</h:commandLink>
						
					</td>
					<td width="10px">							 
							<h:commandLink  styleClass="noborder" id="btnremover" immediate="true"
								action="#{docenteExterno.iniciarRemover}">
								<f:param name="id" value="#{docente.id}"/>
								<h:graphicImage url="/img/delete.gif" title="Remover"/>							
							</h:commandLink>
					</td>
					<td width="10px">							 
							<h:commandLink  styleClass="noborder" id="btnadicionar" immediate="true"
								action="#{usuarioDocenteExterno.iniciar}">
								<f:param name="id" value="#{docente.id}"/>
								<h:graphicImage url="/img/adicionar.gif" title="Cadastrar Usuário"/>
							</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
