<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> &gt; Solicitação de Convênio de Estágio</h2>
<a4j:keepAlive beanName="convenioEstagioMBean" />
<c:if test="${convenioEstagioMBean.cadastro}">
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usuário, </b>
		</p> 	
		<p>
		   Verifique se os dados informados estão corretos e selecione a opção Submeter, caso contrário selecione a opção "<< Voltar"
		   para Corrigir os dados necessários.	  
		</p>
		<c:if test="${ convenioEstagioMBean.obj.submetido }"> 	
			<p>
			   Após a Submissão do Cadastro de Convênio, será notificado a PROPLAN, onde será analisada e retornado o parecer final.
			</p>
		</c:if>
	</div>
</c:if>

<table class="visualizacao">
	<caption>Dados da Solicitação de Convênio de Estágio</caption>
	<tr>
		<th style="width: 35%;">Tipo do Convênio:</th>
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
		<th>O Concedente é um Órgão Federal:</th>
		<td>
			<ufrn:format type="simnao" valor="${convenioEstagioMBean.obj.orgaoFederal}"></ufrn:format>
		</td>
	</tr>
	<tr>	
		<th>
		    O Concedente é um Agente de Integração:
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
		<th>Município:</th>
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
		<td colspan="2" class="subFormulario">Dados do Responsável</td>
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
		<th>Órgão de Expedição:</th>
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
			<td colspan="2" class="subFormulario">Dados da Análise</td>
		</tr>	
		<tr>
			<th>Situação:</th>
			<td>
				<h:outputText value="#{convenioEstagioMBean.obj.status.descricao}"/>
			</td>
		</tr>	
		<c:if test="${convenioEstagioMBean.obj.aprovado}">
			<tr>
				<th>Número do Convênio:</th>
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
						<th>Código do Projeto:</th>
						<td>
							<h:outputText value="#{convenioEstagioMBean.obj.concedente.codigoProjeto}"/> 							
						</td>					
					</tr>					
				</c:if>
			</c:if>		
		</c:if>	
		<c:if test="${convenioEstagioMBean.obj.recusado}">
			<tr>
				<th>Motivo da Análise:</th>
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