<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style type="text/css">
span.info {	font-size: 0.9em;color: #666;}

h4.subTitulo {
	background: #EEE;color: #555;font-variant: small-caps;padding: 2px 10px;}

a.button {margin: 0 2px;border: 1px solid #AAAAAA;font-family: Verdana, sans-serif;font-size: 1em;}
</style>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>

<script type="text/javascript">
	tinyMCE.init({
    	mode : "textareas",
        theme : "advanced",
        width : "100%",
        height : "400",
        language : "pt",
        plugins : "preview, emotions, iespell, print, fullscreen, advhr, directionality, searchreplace, insertdatetime, paste",
        plugin_preview_width : "500",
        plugin_preview_height : "600",
        extended_valid_elements : "hr[class|width|size|noshade]",
        plugin_insertdate_dateFormat : "%Y-%m-%d",
        plugin_insertdate_timeFormat : "%H:%M:%S"
	});
</script>
<f:view>
	<a4j:keepAlive beanName="portalPublicoPrograma"/>
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
		<ufrn:subSistema /> &gt; Apresentação
	</h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário(a),</p>
		<br />
		<p>As informações colocadas aqui irão aparecer na página pública do programa, departamento ou curso.</p>
	</div>

	<h:form id="formulario" enctype="multipart/form-data"
		onsubmit="alert(visualizar.value);">

		<table class="formulario" width="99%">
			<caption class="formulario">Detalhes do Site</caption>

			<tr>
				<td colspan="2" class="subFormulario">Endereços de acesso</td>
			</tr>
			
			<%-- Somente para os programas e departamentos --------------------------- --%>
			<c:if test="${detalhesSite.obj.unidade.programa 
			|| detalhesSite.obj.unidade.departamento || detalhesSite.obj.curso.graduacao}">
				<tr>
					<th class="required" width="25%">Endereço Oficial:</th>
					<td>
						${detalhesSite.urlOficial}/
						<h:inputText size="10"
						maxlength="15" value="#{detalhesSite.obj.url}"  /> 
						<br />
						<span class="info"> (Não utilizar acentuação ou espaços
						neste campo) </span>
					</td>
				</tr>
			</c:if>
			<%-- ---------------------------------------------------------------------- --%>
			
			<tr>
				<th>Endereço Alternativo :</th>
				<td><h:inputText size="72" maxlength="255"
					value="#{detalhesSite.obj.siteExtra}" 
					onkeypress="return formataURL(this,event)" /> <br />
				<span class="info"> Caso possua uma página web,
				informe aqui seu endereço </span></td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Texto Introdutório</td>
			</tr>
		
			<tr>
				<td colspan="2">
					<input type="hidden" name="aba" id="aba" />
					<div id="tabs" class="reduzido">
					<div id="pt" class="aba">
						<h:inputTextarea
						value="#{detalhesSite.obj.introducao}" id="textoIntroducao"
						rows="10" style="width: 95%" />
					</div>
					<c:if test="${!detalhesSite.obj.unidade.departamento && !detalhesSite.obj.unidade.unidadeAcademicaEspecializada}">
						<div id="en" class="aba">
							<h:inputTextarea
							value="#{detalhesSite.obj.introducaoEN}" id="textoIntroducaoEN"
							rows="10" style="width: 95%" /></div>
					</c:if>
					<%-- 
						Comentado até a disponibilização dos demais textos traduzidos
						<div id="fr" class="aba"><h:inputTextarea
							value="#{detalhesSite.obj.introducaoFR}" id="textoIntroducaoFR"
							rows="10" style="width: 95%" /></div>
						<div id="es" class="aba"><h:inputTextarea
							value="#{detalhesSite.obj.introducaoES}" id="textoIntroducaoES"
							rows="10" style="width: 95%" /></div>
					--%>
					</div>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Imagens personalizadas</td>
			</tr>
			
			<%-- Somente para os programas e departamentos --------------------------- 		--%>
			<tr>
				<td colspan="2" align="left">
				<h4 class="subTitulo">Foto de apresentação</h4>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
				<a4j:outputPanel id="panelFoto">
				<c:choose>
					<c:when test="${!detalhesSite.excluirFoto && detalhesSite.obj.idFoto != null}">
					<table  class="subFormulario" width="100%">
					<tr>
						<td align="center" colspan="2"><img src="/sigaa/verFoto?idFoto=${detalhesSite.obj.idFoto}&key=${ sf:generateArquivoKey(detalhesSite.obj.idFoto) }" /></td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							<h:commandLink title="Excluir Foto" actionListener="#{detalhesSite.excluirFoto}">
								<a4j:support event="onclick" reRender="panelFoto" onsubmit="#{confirm}"/>
								<h:graphicImage value="/img/garbage.png" /> Excluir Foto da Apresentação
							</h:commandLink>
						</td>
					</tr>	
					</table>
					</c:when> 
					<c:otherwise>
					<table  class="subFormulario" width="100%">
					<tr>
						<th>Selecione a foto de apresentação:</th>
						<td>
							<ufrn:help img="/img/ajuda.gif">
								O tamanho desta imagem é livre porém recomendamos utilizar o tamanho 680x220 pixels.
							</ufrn:help>
							<t:inputFileUpload	value="#{detalhesSite.foto}" size="50" id="foto" />
						</td>
					</tr>
					</table>
					</c:otherwise>
				</c:choose>
				</a4j:outputPanel>
				</td>
			</tr>
			<%-- ---------------------------------------------------------------------- 		--%>
			
			
			<tr>
				<td colspan="2">
				<h4 class="subTitulo">Logotipo</h4>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
				<a4j:outputPanel id="panelLogo">
				<c:choose>
					<c:when test="${!detalhesSite.excluirLogo && detalhesSite.obj.idLogo != null}">	
						<table  class="subFormulario" width="100%">
						<tr>
							<td align="center" colspan="2">
								<img src="/sigaa/verFoto?idFoto=${detalhesSite.obj.idLogo}
									&key=${ sf:generateArquivoKey(detalhesSite.obj.idLogo) }" />
							</td>
						</tr>
						<tr>
							<td align="center" colspan="2">
								<h:commandLink title="Excluir Logo"  actionListener="#{detalhesSite.excluirLogo}">
									<a4j:support event="onclick" reRender="panelLogo" onsubmit="#{confirm}"/>
									<h:graphicImage value="/img/garbage.png" /> Excluir Logotipo
								</h:commandLink>
							</td>
						</tr>
						</table>
					</c:when> 
					<c:otherwise>	
						<table class="subFormulario" width="100%">
						<tr>
							<th>Selecione a logo do portal:</th>
							<td>
								<ufrn:help img="/img/ajuda.gif">
									O tamanho desta imagem é livre porém recomendamos utilizar o tamanho 200x90 pixels.
								</ufrn:help>
								<t:inputFileUpload value="#{detalhesSite.logo}" size="50" id="logo" />
							</td>
						</tr>
						</table>
					</c:otherwise>
				</c:choose>	
				</a4j:outputPanel>
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
		
						<h:commandButton id="salvar" value="#{detalhesSite.confirmButton}"
							action="#{detalhesSite.cadastrar}"
							onclick="javascript:this.form.target='_self';"/>

						<h:commandButton
							id="cancelar" value="Cancelar"
							onclick="this.form.target='_self'; #{confirm}"
							action="#{detalhesSite.cancelar}" /> 
						
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<br>
	<center>
		<html:img page="/img/required.gif"	style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
		<br><br>
	</center>
	
	<script>
		var Abas = {
		    init : function(){
		        var abas = new YAHOO.ext.TabPanel('tabs');
		        	abas.addTab('pt', "Português");
		        	eval(${!detalhesSite.obj.unidade.departamento && !detalhesSite.obj.unidade.unidadeAcademicaEspecializada?'abas.addTab(\'en\', \'Inglês\')':''});
		        	abas.activate('pt');
				  
		    }
		};
		
		YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
		function setAba(aba) {
			document.getElementById('aba').value = aba;
		}
	</script>
	
	<script type="text/javascript">$('formulario:textoIntroducao').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>