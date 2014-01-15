<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="form" >

<h2 class="title"><ufrn:subSistema /> > Abertura de Livro de Registro de ${livroRegistroDiplomas.obj.tipoRegistroDescricao}</h2>

<div class="descricaoOperacao">
	Caro usuário, informe os dados do Livro de Registro de Diplomas.
	<c:if test="${!livroRegistroDiplomas.obj.vazio and livroRegistroDiplomas.obj.id > 0}">
	<br/>Uma vez que existem diplomas registrados neste livro, é permitido apenas a alteração parcial dos dados do livro.
	</c:if>
</div>

<div class="infoAltRem" style="font-variant: small-caps;">
	<h:graphicImage value="/img/buscar.gif"style="overflow: visible;"/>: Listar os últimos livros criados.
</div>
<table class="formulario" width="99%">
	<caption class="formulario">Dados do Livro</caption>
	<tbody>
		<tr>
			<th width="30%" class="required">Nível de Ensino:</th>
			<td>
				<h:selectOneMenu value="#{livroRegistroDiplomas.obj.nivel}"
					onchange="submit()" id="nivelEnsino" disabled="#{not livroRegistroDiplomas.obj.vazio}"
					valueChangeListener="#{ livroRegistroDiplomas.nivelListener }">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
					<f:selectItems value="#{livroRegistroDiplomas.niveisHabilitadosCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<!-- O teste abaixo garante que o usuário selecionou um nível de ensino -->
		<h:panelGroup rendered="#{livroRegistroDiplomas.obj.graduacao || livroRegistroDiplomas.obj.latoSensu || livroRegistroDiplomas.obj.strictoSensu}">
			<tr>
				<th class="required">Título:</th>
				<td>
					<h:inputText value="#{livroRegistroDiplomas.obj.titulo}" id="titulo"  size="14" maxlength="12" onkeyup="CAPS(this)" />
					<a onclick="PainelUltimosTitulos.show()" href="#">
						<h:graphicImage alt="Últimos livros criados" url="/img/buscar.gif" title="Últimos livros criados"/>
					</a>
				</td>
			</tr>
			<c:if test="${livroRegistroDiplomas.obj.graduacao}">
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{livroRegistroDiplomas.obj.registroExterno}" id="externoCheck"  disabled="#{not livroRegistroDiplomas.obj.vazio}" onclick="submit()"/>
					</th>
					<td>O livro será utilizado para registrar ${livroRegistroDiplomas.obj.tipoRegistroDescricao} de Instituições Externas</td>
				</tr>
			</c:if>
			<tr>
				<th>
					<h:selectBooleanCheckbox value="#{livroRegistroDiplomas.obj.livroAntigo}" id="antigoCheck" disabled="#{not livroRegistroDiplomas.obj.vazio}" onclick="submit()" />
				</th>
				<td>Este é um livro de registro antigo (anterior ao Registro de Diplomas no SIGAA)</td>
			</tr>
			<c:if test="${livroRegistroDiplomas.obj.graduacao}">
				<c:if test="${livroRegistroDiplomas.obj.registroExterno}" >
					<tr>
						<th class="required">
							Instituição:
						</th>
						<td>
							<h:inputText value="#{livroRegistroDiplomas.obj.instituicao}" id="instituicaoExterna"  
								size="40" maxlength="80" onkeyup="CAPS(this)" disabled="#{not livroRegistroDiplomas.obj.vazio}"/>
						</td>
					</tr>
				</c:if>
				<c:if test="${!livroRegistroDiplomas.obj.registroExterno}" >
					<tr>
						<th class="rotulo">
							Instituição:
						</th>
						<td>
							<h:outputText rendered="#{not livroRegistroDiplomas.obj.registroExterno}"
								value="Universidade Federal do Rio Grande do Norte" />
						</td>
					</tr>
				</c:if>
			</c:if>
			<tr>
				<th class="required">
					<h:outputText rendered="#{livroRegistroDiplomas.obj.livroAntigo}" value="Número exato de páginas:"/>
					<h:outputText rendered="#{not livroRegistroDiplomas.obj.livroAntigo}" value="Número sugerido de páginas:"/>
				</th>
				<td>
					<h:inputText value="#{livroRegistroDiplomas.obj.numeroSugeridoFolhas}" id="paginas" size="5" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" disabled="#{not livroRegistroDiplomas.obj.vazio}"/>
				</td>
			</tr>
			<c:if test="${livroRegistroDiplomas.obj.graduacao || livroRegistroDiplomas.obj.latoSensu}">
				<tr>
					<th class="rotulo">Número de Registros por Folha:</th>
					<td>
						<h:outputText value="#{livroRegistroDiplomas.obj.numeroRegistroPorFolha}" id="numeroRegistros" />
					</td>
				</tr>
			</c:if>
			<c:if test="${livroRegistroDiplomas.obj.strictoSensu}">
				<tr>
					<th class="required">Número de Registros por Folha:</th>
					<td>
						<h:selectOneMenu value="#{livroRegistroDiplomas.obj.numeroRegistroPorFolha}" id="numeroRegistrosStricto">
							<f:selectItem itemLabel="3" itemValue="3"/>
							<f:selectItem itemLabel="4" itemValue="4"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${livroRegistroDiplomas.obj.graduacao}">
				<c:if test="${not livroRegistroDiplomas.obj.registroExterno}">
					<tr>
						<th class="required">
							Curso:
						</th>
						<td>
							<h:selectOneMenu value="#{livroRegistroDiplomas.idCurso}" id="curso" style="width: 90%;">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<c:if test="${livroRegistroDiplomas.obj.graduacao}">
								<h:commandButton id="adicionarCurso" value="Adicionar Curso neste Livro" action="#{livroRegistroDiplomas.adicionarCurso}" rendered="#{not livroRegistroDiplomas.obj.registroExterno}"/>
							</c:if>
					</td>
					</tr>
				</c:if>
				<c:if test="${livroRegistroDiplomas.obj.registroExterno}">
					<tr>
						<th>
							Curso:
						</th>
						<td>
							<b>Todos os cursos.</b>
						</td>
					</tr>
				</c:if>
				<tr>
					<td colspan="2">
						<c:if test="${not livroRegistroDiplomas.obj.registroExterno}">
							<c:if test="${not empty livroRegistroDiplomas.obj.cursosRegistrados}">
								<div class="infoAltRem">
									<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover curso<br/>
								</div>
								<table class="listagem" id="lista">
									<caption>Cursos Registrados Neste Livro</caption>
									<thead>
										<tr>
											<td><b>Curso</b></td>
											<td></td>
										</tr>
									</thead>
									<tbody>
									<c:forEach items="#{livroRegistroDiplomas.obj.cursosRegistrados}" var="item" varStatus="status">
										<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
											<td>${item.descricao}</td>
											<td width="5%">
												<h:commandLink id="remover" title="Remover curso" action="#{livroRegistroDiplomas.removerCurso}" onclick="#{confirmDelete}" >
													<f:param name="id" value="#{item.id}" />
													<h:graphicImage url="/img/delete.gif"/>
												</h:commandLink>
											</td>
										</tr>
									</c:forEach>
									</tbody>
								</table>
							</c:if>
							<c:if test="${empty livroRegistroDiplomas.obj.cursosRegistrados}">
								<div align="center">Não há cursos especificados para registro neste livro.</div>
							</c:if>
						</c:if>
					</td>
				</tr>
			</c:if>
		</h:panelGroup>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="cadastrar" value="#{livroRegistroDiplomas.confirmButton}" action="#{livroRegistroDiplomas.cadastrar}" />
				<h:commandButton id="voltar" value="<< Voltar" action="#{livroRegistroDiplomas.backList}" rendered="#{livroRegistroDiplomas.obj.id != 0}"/>
				<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{livroRegistroDiplomas.cancelar}" />
			</td>
		</tr>
	</tfoot>
</table>

	<br>
	<center><html:img page="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</h:form>
</f:view>

<script>
var PainelUltimosTitulos = (function() {
	var painel;
	return {
        show : function(){
	        	var p = getEl('painel-ultimosTitulos');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-ultimosTitulos", {
	   	 		   autoCreate: true,
				   title: 'Últimos livros criados',
                   proxyDrag: true,
                   constraintoviewport: false,
	               width: 700,
	               height: 330,
	               resizable: true
            	});

	       	 	painel.show();

				var um = painel.body.getUpdateManager();
				um.disableCaching = false;
				um.update({
					 url: '/sigaa/diplomas/livro_registro_diploma/ultimos_titulos.jsf',
					 discardUrl: true,
					 nocache: true,
					 text: 'Carregando...'
					 });
        }
	};
})();

</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
