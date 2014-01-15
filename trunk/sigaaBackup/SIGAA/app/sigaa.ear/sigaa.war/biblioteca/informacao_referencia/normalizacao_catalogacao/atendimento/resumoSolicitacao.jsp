<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%-- 
<f:view>

<h2> <ufrn:subSistema/> &gt; Solicita��o de Normaliza��o e Cataloga��o &gt; <h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.confirmButton}"/></h2>

<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaCatalografica && solicitacaoNormalizacaoCatalogacaoMBean.validar}">
	<div class="descricaoOperacao">
		<p><strong> Declaro para os devidos fins, que recebi o material do usu�rio ${solicitacaoNormalizacaoCatalogacaoMBean.obj.solicitante} nesta presente data.</strong> </p>
	</div>
</c:if>


<a4j:keepAlive beanName="solicitacaoNormalizacaoCatalogacaoMBean"></a4j:keepAlive>

<h:form id="formOperacao">



<table class="formulario" width="98%">

		<caption class="formulario">Solicita��o de normaliza��o e cataloga��o</caption>
		
		<c:if test="${not empty solicitacaoNormalizacaoCatalogacaoMBean.obj.discente}">
			<tr>
				<th width="40%">Solicitante:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.discente}"/></td>
			</tr>
			
			<th>Categoria:</th>
			
			<c:if test="${  ( solicitacaoNormalizacaoCatalogacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
					|| solicitacaoNormalizacaoCatalogacaoMBean.obj.discente.lato 
					|| solicitacaoNormalizacaoCatalogacaoMBean.obj.discente.mestrado 
					|| solicitacaoNormalizacaoCatalogacaoMBean.obj.discente.doutorado}">
				
				<td>Aluno de P�s-Gradua��o</td>
			</c:if>
			<c:if test="${ ! ( solicitacaoNormalizacaoCatalogacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
					&& !solicitacaoNormalizacaoCatalogacaoMBean.obj.discente.lato 
					&& !solicitacaoNormalizacaoCatalogacaoMBean.obj.discente.mestrado 
					&& !solicitacaoNormalizacaoCatalogacaoMBean.obj.discente.doutorado}">
				<td>Aluno de Gradua��o</td>
			</c:if>
			
			<tr>
				<th>Curso:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.discente.curso}"/></td>
			</tr>
			
		</c:if>
		
		<c:if test="${not empty solicitacaoNormalizacaoCatalogacaoMBean.obj.servidor}">
			<tr>
				<th>Solicitante:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.servidor}"/></td>
			</tr>
			<tr>
				<th>Categoria:</th>
				<td>
					<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.servidor.categoria.docente}" >
						Docente
					</c:if>
					<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.servidor.categoria.tecnico}" >
						T�cnico Administrativo
					</c:if>
				</td>
			</tr>
			<tr>
				<th>Lota��o:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.servidor.unidade}"/></td>
			</tr>
		</c:if>
		
		<tr>
			<th>Telefone:</th>
			<td> ${solicitacaoNormalizacaoCatalogacaoMBean.obj.pessoa.telefone} </td>
		</tr>
		
		<tr>
			<th>Celular:</th>
			<td> ${solicitacaoNormalizacaoCatalogacaoMBean.obj.pessoa.celular} </td>
		</tr>
		
		<tr>
			<th>Email:</th>
			<td> ${solicitacaoNormalizacaoCatalogacaoMBean.obj.pessoa.email} </td>
		</tr>
		
		<tr>
			<th>Situa��o da Solicita��o:</th>
			<td>
				<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.solicitado}">
					<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.descricaoSituacao}"/>
				</c:if>
				<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.validado}">
					<h:outputText style="color:grey;" value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.descricaoSituacao}"/>
				</c:if>
				<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.atendido}">
					<h:outputText style="color:green;"  value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.descricaoSituacao}"/>
				</c:if>
				<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.cancelado}">
					<h:outputText style="color:red;"  value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.descricaoSituacao}"/>
				</c:if>
			</td>
		</tr>
		
		<tr>
			<th>Data Solicita��o:</th>
			<td><h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.dataCadastro}"/></td>
		</tr>
		
		<tr>
			<th>Biblioteca:</th>
			<td><h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.biblioteca.descricao}"/></td>
		</tr>
		
		<tr>
			<th>Tipo Documento:</th>
			<td>
				<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.tipoDocumento.denominacao}"/>
				<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.tipoDocumento.tipoDocumentoOutro}">
							(  ${solicitacaoNormalizacaoCatalogacaoMBean.obj.outroTipoDocumento}  )
				</c:if>
			</td>	
		</tr>
		
		<tr>
			<th style="vertical-align:top;">Aspectos a normalizar:</th>
			<td>
				Trabalho no todo: 
				<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.trabalhoTodo}">
			      <f:converter converterId="convertSimNao"/>
				</h:outputText>
				<br/>
				
				<c:if test="${! solicitacaoNormalizacaoCatalogacaoMBean.obj.trabalhoTodo}">
				 
					Refer�ncias:
					<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.referencias}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
					
					Cita��es:
					<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.citacoes}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
					
					Estrutura do Trabalho:
					<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.estruturaDoTrabalho}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
					
					Pr�-textuais:
					<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.preTextuais}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
					
					Pr�-textuais:
					<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.proTextuais}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
				
				</c:if>
				
				Ficha Catalogr�fica:
				<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaCatalografica}">
			      <f:converter converterId="convertSimNao"/>
				</h:outputText>
				<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaCatalografica}">
					 - N�mero de folhas: <h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.numeroPaginas}"/>
				</c:if>
				<br/>
				 
				<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.outrosAspectosNormalizacao}">
					Outros Aspactos Normaliza��o: <h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.descricaoOutrosAspectosNormalizacao}"/>
				</c:if> 
				
			</td>
		</tr>
		
		<tr>
			<th>Palavras-chave:</th>
			<td>
				<h:outputText id="palavrasChaves" value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.palavrasChaveString}"/>
			</td>
		</tr>
		
		<tr>
			<th>Autoriza Descarte?</th>
			<td>
				<h:outputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.autorizaDescarte}">
			      <f:converter converterId="convertSimNao"/>
				</h:outputText>
			</td>
		</tr>
		
		<c:if test="${solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaCatalografica && solicitacaoNormalizacaoCatalogacaoMBean.atender}">
		<tr>
			<td colspan="2">
				<table width="100%">
					<caption>Ficha Catalogr�fica</caption>
					<tr>
						<th class="required">T�tulo:</th>
						<td><h:inputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaGerada.titulo}" size="70" maxlength="300" id="txtTitulo"/></td>
					</tr>
					<tr>
						<th class="required">Institui��o:</th>
						<td><h:inputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaGerada.instituicao}" size="70" maxlength="300" id="txtInstituicao"/></td>
					</tr>
					<tr>
						<th class="required">Orientador:</th>
						<td><h:inputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaGerada.orientador}" size="70" maxlength="150" id="txtOrientador"/> </td>
					</tr>
					<tr>
						<th class="required">Total de folhas:</th>
						<td><h:inputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaGerada.folhas}" size="4" maxlength="4" id="txtFolhas" onkeypress="return ApenasNumeros(event);"/></td>
					</tr>
					<tr>
						<th class="required">Local:</th>
						<td><h:inputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaGerada.local}" size="20" maxlength="50" /></td>
					</tr>
					<tr>
						<th class="required">Ano:</th>
						<td><h:inputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaGerada.data}" size="10" maxlength="10" /></td>
					</tr>
					<tr>
						<th class="required">Ilustrado:</th>
						<td>
							<h:selectOneRadio value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaGerada.ilustrado}" id="ilustrado">
								<f:selectItem itemLabel="Sim" itemValue="true"/>
								<f:selectItem itemLabel="N�o" itemValue="false"/>
							</h:selectOneRadio> 
						</td>
					</tr>
					<tr>
						<th class="required">CDU:</th>
						<td> <h:inputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaGerada.cdu}" size="10" maxlength="15" id="txtCdu"/> </td>
					</tr>
					<tr>
						<th class="required">Biblioteca:</th>
						<td> <h:inputText value="#{solicitacaoNormalizacaoCatalogacaoMBean.obj.fichaGerada.biblioteca}" size="40" maxlength="200" id="txtBiblioteca"/> (${solicitacaoNormalizacaoCatalogacaoMBean.obj.biblioteca.descricao})</td>
					</tr>
				</table>
			</td>
		</tr>
		</c:if>
		
		
		<tfoot>
			<tr>
				<td colspan="2">
						<h:commandButton value="#{solicitacaoNormalizacaoCatalogacaoMBean.confirmButton}" action="#{solicitacaoNormalizacaoCatalogacaoMBean.confirmar}" />
						<h:commandButton value="<< Voltar" action="#{solicitacaoNormalizacaoCatalogacaoMBean.telaListaSolicitacoes}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoNormalizacaoCatalogacaoMBean.cancelar}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
</table>
</h:form>
--%>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>