<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<script src="/sigaa/javascript/site/jquery.vreboton.ColorPicker.js"></script>
<link rel="stylesheet" href="/sigaa/javascript/site/jquery.vreboton.ColorPicker.css" />	
<f:view>
	<a4j:keepAlive beanName="portalPublicoPrograma"/>
	<a4j:keepAlive beanName="portalPublicoCurso"/>
	<c:if test="${detalhesSite.portalDocente}"> 
	<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>
	<c:if test="${detalhesSite.portalCoordenadorStricto}">
	<%@include file="/stricto/menu_coordenador.jsp"%>
	</c:if>
	<c:if test="${detalhesSite.portalCoordenadorGraduacao}">
	<%@include file="/graduacao/menu_coordenador.jsp"%>
	</c:if>
	
	<h:messages showDetail="true"></h:messages>
	
	<h:outputText value="#{detalhesSite.create}"></h:outputText>
	
	<h2 class="title">
		<ufrn:subSistema /> &gt; Configura��o das Cores do Portal
	</h2>

	<div class="descricaoOperacao">
		<p>Caro Usu�rio(a),</p>
		<br />
		<p>As informa��es colocadas aqui ir�o definir as cores de alguns elementos da p�gina p�blica do programa.</p>
	</div>
	

	<br clear="all"/>		
	<h:form id="formulario">
		<center>
				<div class="infoAltRem">
					<h:commandLink action="#{detalhesSite.templatePadrao}">
						<img src="${ctx}/img/refresh.png" align="absmiddle" style="top:0px;" />	Restaurar as cores padr�es
					</h:commandLink>
				</div>
		</center>	
		<br clear="all"/>
		<table class="formulario" width="100%">
			<caption>Formul�rio de Configura��o das Cores do Portal</caption>
			<tbody>
				
				
				<tr>
					<td class="subFormulario" colspan="6"> Topo do Site </td>
				</tr>
				<tr>
					<th>Sigla:</th>
					<td>
		                <h:inputText styleClass="Multiple" maxlength="7" value="#{detalhesSite.obj.templateSite.corSigla}"/>
		        	</td>
					<th>T�tulo</th>
					<td><h:inputText  styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corTitulo}"/></td>
					<th>Sub-T�tulo</th>
					<td><h:inputText  styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corSubTitulo}"/></td>
				</tr>
		
				<tr>
					<td class="subFormulario" colspan="6"> Menu do Site </td>
				</tr>
				<tr>
					<th>Fundo do Menu</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corFundoMenu}"/></td>
					<th>Texto do Menu e Sub-Menu</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corTitMenu}"/></td>
					<th>Fundo do Sub-Menu</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corFundoSubMenu}"/></td>
				</tr>
				<tr>	
					<th>Separador do Sub-Menu</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corSepSubMenu}"/></td>
					<th></th>
					<td></td>
					<th></th>
					<td></td>
				</tr>
				
		
				<tr>
					<td class="subFormulario" colspan="6"> Conte�do do Site </td>
				</tr>
				<tr>
					<th>T�tulo do Conte�do</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corTitCont}"/></td>
					<th>Sub-T�tulo do Conte�do</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corSubTitCont}"/></td>
					<th>Texto do Conte�do</th>
					<td><h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corTextoCont}"/></td>
				</tr>
				<tr>
					<th>Link</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corLink}"/></td>
					<th>Link(Ativo)</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corLinkHover}"/></td>
					<th></th>
					<td></td>
				</tr>
				
			
				<tr>
					<td class="subFormulario" colspan="6"> Listagem/Formul�rio do Site </td>
				</tr>
				<tr>	
					<th>Fundo do Cabe�alho/Rodap� da Tabela</th>
					<td><h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corFundoTabela}"/></td>
					<th>Texto do Cabe�alho/Rodap� da Tabela</th>
					<td><h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corTituloTabela}"/></td>
					<th>Fundo do Sub Cabe�alho da Tabela</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corSubCabTab}"/></td>
				</tr>
				<tr>
					<th >Texto do Sub Cabe�alho da Tabela</th>
					<td><h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corTitSubCabTab}"/></td>
					<th>Fundo da Linha da Tabela (Intercalada)</th>
					<td> <h:inputText styleClass="Multiple" maxlength="7"  value="#{detalhesSite.obj.templateSite.corLinhaImpar}"/></td>
					<th></th>
					<td></td>
				</tr>
				
				</tr>
			</tbody>
			<tfoot>
					<tr>
						<td colspan="6">
			
						<h:commandButton id="visualizar" value="Pr�-Visualizar"
								action="#{detalhesSite.previewPrincipal}"
								onclick="javascript: this.form.target='_blank';"/>
					
						<h:commandButton id="salvar" value="Salvar"
								action="#{detalhesSite.cadastrar}"
								onclick="javascript: this.form.target='_self';"/>
		
						<h:commandButton
								id="cancelar" value="Cancelar"
								onclick="this.form.target='_self'; #{confirm}"
								action="#{detalhesSite.cancelar}" /> 
						
						</td>
					</tr>
			</tfoot>
		</table>
	</h:form>
		
<rich:jQuery selector=".Multiple" query="attachColorPicker()" timing="onload" />
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>