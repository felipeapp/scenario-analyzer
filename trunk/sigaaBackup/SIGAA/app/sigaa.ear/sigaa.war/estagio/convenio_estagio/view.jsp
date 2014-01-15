<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> &gt; Solicita��o de Conv�nio de Est�gio</h2>
<a4j:keepAlive beanName="convenioEstagioMBean" />
<c:if test="${convenioEstagioMBean.cadastro}">
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usu�rio, </b>
		</p> 	
		<p>
		   Verifique se os dados informados est�o corretos e selecione a op��o Submeter, caso contr�rio selecione a op��o "<< Voltar"
		   para Corrigir os dados necess�rios.	  
		</p>
		<c:if test="${ convenioEstagioMBean.obj.submetido }"> 	
			<p>
			   Ap�s a Submiss�o do Cadastro de Conv�nio, ser� notificado a PROPLAN, onde ser� analisada e retornado o parecer final.
			</p>
		</c:if>
	</div>
</c:if>

<table class="visualizacao">
	<caption>Dados da Solicita��o de Conv�nio de Est�gio</caption>
	<tr>
		<th style="width: 35%;">Tipo do Conv�nio:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.obj.tipoConvenio.descricao}"/>
		</td>
	</tr>	
	<tr>
		<th>Tipo de Oferta de Vaga:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.obj.tipoOfertaVaga}" />				
		</td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario">Dados do Concedente</td>
	</tr>
	<tr>
		<th>CNPJ:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.obj.concedente.pessoa.cpfCnpjFormatado}"/>
		</td>
	</tr>
	<tr>
		<th>Nome:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.obj.concedente.pessoa.nome}"/>																																				
		</td>
	</tr>
	<tr>
		<th>O Concedente � um �rg�o Federal:</th>
		<td>
			<ufrn:format type="simnao" valor="${convenioEstagioMBean.obj.orgaoFederal}"></ufrn:format>
		</td>
	</tr>
	<tr>	
		<th>
		    O Concedente � um Agente de Integra��o:
		</th>	
		<td colspan="2">
			<ufrn:format type="simnao" valor="${convenioEstagioMBean.obj.agenteIntegrador}"></ufrn:format>
		</td>
	</tr>
	<tr>
		<th>CEP:</th>
		<td>
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.cep}
		</td>
	</tr>
	<tr>
		<th>Logradouro:</th>
		<td>
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.logradouro}, 
			<b>N.&deg;:</b>
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.numero}
		</td>
	</tr>
	<tr>
		<th>Bairro:</th>
		<td>
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.bairro}
		</td>
	</tr>
	<tr>
		<th>Complemento:</th>
		<td>
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.complemento}
		</td>
	</tr>
	<tr>
		<th>UF:</th>
		<td>
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.unidadeFederativa.sigla}</td>
	</tr>
	<tr>
		<th>Munic�pio:</th>
		<td>
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.municipio.nome}
		</td>
	</tr>
	<tr>
		<th>Tel. Fixo:</th>
		<td>
			(${convenioEstagioMBean.obj.concedente.pessoa.codigoAreaNacionalTelefoneFixo})
			 ${convenioEstagioMBean.obj.concedente.pessoa.telefone}</td>
	</tr>
	<tr>
		<th>Tel. Celular:</th>
		<td>
			(${convenioEstagioMBean.obj.concedente.pessoa.codigoAreaNacionalTelefoneCelular})
			 ${convenioEstagioMBean.obj.concedente.pessoa.celular}					
		</td>
	</tr>	 
	<tr>
		<td colspan="2" class="subFormulario">Dados do Respons�vel</td>
	</tr>
	<tr>
		<th>CPF:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.cpfCnpjFormatado}"/>	
		</td>
	</tr>
	<tr>
		<th>Nome:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.nome}"/>
		</td>
	</tr>
	<tr>
		<th>RG:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.identidade.numero}"/></td>
	</tr>
	<tr>
		<th>�rg�o de Expedi��o:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.identidade.orgaoExpedicao}"/>
		</td>
	</tr>							
	<tr>
		<th>Cargo:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.concedenteEstagioPessoa.cargo}"/>
		</td>
	</tr>		
	<tr>
		<th>Email:</th>
		<td>
			<h:outputText value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.email}"/>
		</td>
	</tr>
	<c:if test="${not empty convenioEstagioMBean.obj.dataAnalise}">		
		<tr>
			<td colspan="2" class="subFormulario">Dados da An�lise</td>
		</tr>	
		<tr>
			<th>Situa��o:</th>
			<td>
				<h:outputText value="#{convenioEstagioMBean.obj.status.descricao}"/>
			</td>
		</tr>	
		<c:if test="${convenioEstagioMBean.obj.aprovado}">
			<tr>
				<th>N�mero do Conv�nio:</th>
				<td>
					<h:outputText value="#{convenioEstagioMBean.obj.numeroConvenio}"/>
				</td>
			</tr>			
			<c:if test="${convenioEstagioMBean.obj.agenteInterno}">
				<c:if test="${convenioEstagioMBean.obj.concedente.unidade != null}">	
					<tr>
						<th>Unidade:</th>
						<td>
							<h:outputText value="#{convenioEstagioMBean.obj.concedente.unidade.nome}"/>				
						</td>	
					</tr>
				</c:if>
				<c:if test="${convenioEstagioMBean.obj.concedente.codigoProjeto != null}">
					<tr>
						<th>C�digo do Projeto:</th>
						<td>
							<h:outputText value="#{convenioEstagioMBean.obj.concedente.codigoProjeto}"/> 							
						</td>					
					</tr>					
				</c:if>
			</c:if>		
		</c:if>	
		<c:if test="${convenioEstagioMBean.obj.recusado}">
			<tr>
				<th>Motivo da An�lise:</th>
				<td>
					<h:outputText value="#{convenioEstagioMBean.obj.motivoAnalise}"/>						
				</td>					
			</tr>				
		</c:if>		
	</c:if>					
	<tfoot>
		<tr>
			<td colspan="2">
				<h:form>
					<h:commandButton value="Submeter" action="#{convenioEstagioMBean.cadastrar}" id="btSubmeter" rendered="#{convenioEstagioMBean.cadastro}"/>
					<h:commandButton value="<< Voltar" action="#{convenioEstagioMBean.voltar}" id="btVoltar"/>
					<h:commandButton value="Cancelar" action="#{convenioEstagioMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancel"/>
				</h:form>
			</td>
		</tr>
	</tfoot>
</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>