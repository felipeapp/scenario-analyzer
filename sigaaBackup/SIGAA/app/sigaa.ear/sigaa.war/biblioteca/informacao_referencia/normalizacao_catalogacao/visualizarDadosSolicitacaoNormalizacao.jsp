<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>
<style>
	p{
		 text-indent: 2em;
	}
</style>

<f:view>
<h2> <ufrn:subSistema/> &gt; Solicita��o de Normaliza��o &gt; Visualizar Dados Solicita��o</h2>

<a4j:keepAlive beanName="solicitacaoNormalizacaoMBean"></a4j:keepAlive>

<%-- Parte onde o usu�rio visualiza o comprovante da solicita��o --%>
<c:if test="${! solicitacaoNormalizacaoMBean.obj.atendido}">
	<h:form>
		<table  class="subFormulario" align="center">
			<caption style="text-align: center;">Informa��o Importante</caption>
			<tr>
			<td width="8%" valign="middle" align="center">
				<html:img page="/img/warning.gif"/>
			</td>
			<td valign="middle" style="text-align: justify">
				Por favor imprima o comprovante clicando no �cone ao lado para maior seguran�a dessa opera��o.
			</td>
			<td>
				<table>
					<tr>
						<td align="center">
							<h:commandLink title="Imprimir Comprovante"  target="_blank" action="#{solicitacaoNormalizacaoMBean.telaComprovante}" >
					 			<h:graphicImage url="/img/printer_ok.png" />
					 		</h:commandLink>
					 	</td>
					 </tr>
					 <tr>
					 	<td style="font-size: medium;">
					 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{solicitacaoNormalizacaoMBean.telaComprovante}"  />
					 	</td>
					 </tr>
				</table>
			</td>
			</tr>
		</table>
	<br/>
	</h:form>
</c:if>


<table class="visualizacao" width="90%">
		
		<caption>Solicita��o de normaliza��o n�mero ${solicitacaoNormalizacaoMBean.obj.numeroSolicitacao} </caption>
		
		<c:if test="${not empty solicitacaoNormalizacaoMBean.obj.discente}">
			<tr>
				<th width="40%">Solicitante:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.discente}"/></td>
			</tr>
			
			<th>Categoria:</th>
			
			<c:if test="${  ( solicitacaoNormalizacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
					|| solicitacaoNormalizacaoMBean.obj.discente.lato 
					|| solicitacaoNormalizacaoMBean.obj.discente.mestrado 
					|| solicitacaoNormalizacaoMBean.obj.discente.doutorado}">
				
				<td>Aluno de P�s-Gradua��o</td>
			</c:if>
			<c:if test="${ ! ( solicitacaoNormalizacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
					&& !solicitacaoNormalizacaoMBean.obj.discente.lato 
					&& !solicitacaoNormalizacaoMBean.obj.discente.mestrado 
					&& !solicitacaoNormalizacaoMBean.obj.discente.doutorado}">
				<td>Aluno de Gradua��o</td>
			</c:if>
			
			<tr>
				<th>Curso:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.discente.curso}"/></td>
			</tr>
			
		</c:if>
		
		<c:if test="${not empty solicitacaoNormalizacaoMBean.obj.servidor}">
			<tr>
				<th>Solicitante:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.servidor}"/></td>
			</tr>
			<tr>
				<th>Categoria:</th>
				<td>
					<c:if test="${solicitacaoNormalizacaoMBean.obj.servidor.categoria.docente}" >
						Docente
					</c:if>
					<c:if test="${solicitacaoNormalizacaoMBean.obj.servidor.categoria.tecnico}" >
						T�cnico Administrativo
					</c:if>
				</td>
			</tr>
			<tr>
				<th>Lota��o:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.servidor.unidade}"/></td>
			</tr>
		</c:if>
		
		<tr>
			<th>Telefone:</th>
			<td> ${solicitacaoNormalizacaoMBean.obj.pessoa.telefone} </td>
		</tr>
		
		<tr>
			<th>Celular:</th>
			<td> ${solicitacaoNormalizacaoMBean.obj.pessoa.celular} </td>
		</tr>
		
		<tr>
			<th>Email:</th>
			<td> ${solicitacaoNormalizacaoMBean.obj.pessoa.email} </td>
		</tr>
		
		<tr>
			<th>Data da Solicita��o:</th>
			<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.dataCadastro}"/></td>
		</tr>
		
		<tr>
			<th>Situa��o da Solicita��o:</th>
			<td>
				<c:if test="${solicitacaoNormalizacaoMBean.obj.solicitado}">
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.situacao.descricao}"/>
				</c:if>
				<%-- <c:if test="${solicitacaoNormalizacaoMBean.obj.validado}">
					<h:outputText style="color:grey;" value="#{solicitacaoNormalizacaoMBean.obj.situacao.descricao}"/>
				</c:if> --%>
				<c:if test="${solicitacaoNormalizacaoMBean.obj.atendido}">
					<h:outputText style="color:green;"  value="#{solicitacaoNormalizacaoMBean.obj.situacao.descricao}"/>
				</c:if>
				<c:if test="${solicitacaoNormalizacaoMBean.obj.cancelado}">
					<h:outputText style="color:red;"  value="#{solicitacaoNormalizacaoMBean.obj.situacao.descricao}"/>
				</c:if>
			</td>
		</tr>
		
		<tr>
			<th>Biblioteca:</th>
			<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.biblioteca.descricao}"/></td>
		</tr>
		
		<tr>
			<th>Tipo do Documento:</th>
			<td>
				<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.tipoDocumento.denominacao}"/>
				<c:if test="${solicitacaoNormalizacaoMBean.obj.tipoDocumento.tipoDocumentoOutro}">
							(  ${solicitacaoNormalizacaoMBean.obj.outroTipoDocumento}  )
				</c:if>	
			</td>
		</tr>
			
		<tr>
			<th>Documento Enviado pelo Usu�rio:</th>
			<td>
				<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${solicitacaoNormalizacaoMBean.obj.idTrabalhoDigitalizado}&key=${ sf:generateArquivoKey(solicitacaoNormalizacaoMBean.obj.idTrabalhoDigitalizado) }">
					<h:graphicImage url="/img/porta_arquivos/icones/doc.png" style="border:none" title="Clique aqui para visualizar o documento em formato digital." /> Visualizar
				</a>
			</td>
		</tr>
		
		<tr>
			<th style="vertical-align:top;">Aspectos a normalizar:</th>
			<td>
				Trabalho no todo: 
				<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.trabalhoTodo}">
			      <f:converter converterId="convertSimNao"/>
				</h:outputText>
				<br/>
				
				<c:if test="${! solicitacaoNormalizacaoMBean.obj.trabalhoTodo}">
				 
					Refer�ncias:
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.referencias}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
					
					Cita��es:
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.citacoes}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
					
					Estrutura do Trabalho:
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.estruturaDoTrabalho}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
					
					Pr�-textuais:
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.preTextuais}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
					
					Pr�-textuais:
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.proTextuais}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
				
				</c:if>
				
				<c:if test="${solicitacaoNormalizacaoMBean.obj.outrosAspectosNormalizacao}">
					Outros Aspectos da Normaliza��o: <h:outputText value="#{solicitacaoNormalizacaoMBean.obj.descricaoOutrosAspectosNormalizacao}"/>
				</c:if> 
				
			</td>
		</tr>
		
		<%-- <tr>
			<th>Autoriza Descarte?</th>
			<td>
				<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.autorizaDescarte}">
			      <f:converter converterId="convertSimNao"/>
				</h:outputText>
			</td>
		</tr> --%>
			
		<c:if test="${solicitacaoNormalizacaoMBean.obj.atendido}">
			<tr style="margin-top: 20px;">
				<th style="padding-top: 20px;">Atendida por:</th>
				<td style="padding-top: 20px;">
					${solicitacaoNormalizacaoMBean.obj.atendente}
				</td>
			</tr>
			<tr>
				<th>Data do Atendimento:</th>
				<td>
					<ufrn:format type="dataHora" valor="${solicitacaoNormalizacaoMBean.obj.dataAtendimento}"/>
				</td>
			</tr>
		</c:if>
		
		<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:form id="form">
						<h:commandButton value="<< Voltar" action="#{solicitacaoNormalizacaoMBean.verMinhasSolicitacoes}" />
					</h:form>
				</td>
			</tr>
		</tfoot>
</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>