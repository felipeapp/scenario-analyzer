<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
		<center>
			<b>Número do Comprovante: <h:outputText value="#{bolsaAuxilioMBean.obj.numeroComprovante}" id="comprovante" /> </b>
		</center>
	<br>
		<table style="width: 100%">
			
			<tr>
				<td>
					<b>MATRÍCULA:</b> <h:outputText value="#{bolsaAuxilioMBean.obj.discente.matricula}" id="matricula" />	
				</td>
			</tr>
			
             <tr>
				<td>
					<b>DISCENTE:</b> <h:outputText value="#{bolsaAuxilioMBean.obj.discente.nome}" id="discente" />	
				</td>
			</tr>
			
			<tr>
				<td>
					<b>CURSO:</b> <h:outputText value="#{bolsaAuxilioMBean.obj.discente.curso.descricao}" id="curso"/>
				</td>
			</tr>
			
			<tr>
				<td>
					<b>CEP:</b> <h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.cep}" id="cep"/></td>
			</tr>
			
			<tr>
				<td>
					<b>BAIRRO:</b> <h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.bairro}" id="bairro"/></td>
			</tr>
			
			<tr>
				<td>
					<b>RUA:</b> <h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.logradouro}" id="rua"/></td>
			</tr>
			
			<tr>
				<td>
					<b>NÚMERO:</b> <h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.numero}" id="numero"/></td>
			</tr>
			
			<tr>
				<td>
					<b>CIDADE:</b> <h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.municipio.nome}" id="cidade"/></td>
			</tr>
			
			<tr>
				<td>
					<b>UF:</b> <h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.unidadeFederativa.sigla}" id="uf" /></td>
			</tr>
			
			<tr>
				<td> <b>TIPO DA BOLSA AUXÍLIO:</b>
					<h:outputText value="#{bolsaAuxilioMBean.obj.tipoBolsaAuxilio.denominacao}" /></td>
			</tr>	
			
		</table>
		<br/>
		
		<center>
			<b> INSCRIÇÃO SOLICITADA COM SUCESSO</b>
			<br/>
			
			<b>
			 Lembre-se que o processo seletivo para as bolsas é realizado em várias etapas.
	 		 Portanto os alunos Pré Selecionados deverão comparecer nos dias, hora e local estabelecidos 
	 		 conforme EDITAL para a entrevista social e apresentação de documentos posteriormente.
	 		</b>
 		</center>
		
  <br/>
  
  <center>
  		<small>Autenticação: <br>
  			<h:outputText value="#{bolsaAuxilioMBean.obj.hashAutenticacao}" id="hash" />
  		</small>
  </center>
	<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
</f:view>