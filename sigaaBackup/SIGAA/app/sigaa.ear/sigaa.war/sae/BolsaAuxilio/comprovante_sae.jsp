<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
		<center>
			<b>Número do Comprovante: <h:outputText value="#{bolsaAuxilioMBean.obj.numeroComprovante}" id="comprovante" /> </b>
		</center>
	<br>
		<table style="width: 100%">
			
			<tr>
				<td>
					<b>Matrícula:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.matricula}" id="matricula" />	
				</td>
			</tr>
			
             <tr>
				<td>
					<b>Discente:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.nome}" id="discente" />	
				</td>
			</tr>
			
			<tr>
				<td>
					<b>Curso:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.curso.descricao}" id="curso"/>
				</td>
			</tr>
			
			<tr>
				<td>
					<b>CEP:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.cep}" id="cep"/></td>
			</tr>
			
			<tr>
				<td>
					<b>Bairro:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.bairro}" id="bairro"/></td>
			</tr>
			
			<tr>
				<td>
					<b>Rua:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.logradouro}" id="rua"/></td>
			</tr>
			
			<tr>
				<td>
					<b>Número:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.numero}" id="numero"/></td>
			</tr>
			
			<tr>
				<td>
					<b>Cidade:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.municipio.nome}" id="cidade"/></td>
			</tr>
			
			<tr>
				<td>
					<b>UF:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.unidadeFederativa.sigla}" id="uf" /></td>
			</tr>
			
			<tr>
				<td> <b>Tipo da bolsa auxílio:</b>
					<h:outputText value="#{bolsaAuxilioMBean.obj.tipoBolsaAuxilio.denominacao}" /></td>
			</tr>	
			
		</table>
		<br/>		
	<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
</f:view>