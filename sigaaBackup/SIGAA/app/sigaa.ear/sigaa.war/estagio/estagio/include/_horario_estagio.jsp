<table class="subFormulario" width="100%" border="1">
	<caption>Horário de Entrada e Saída</caption>
 	<thead>
 		<tr>
 			<th rowspan="2" style="text-align: center;">Período</th>
			<th colspan="2" style="text-align: center;">Seg</th>
			<th colspan="2" style="text-align: center;">Ter</th>
			<th colspan="2" style="text-align: center;">Qua</th>
			<th colspan="2" style="text-align: center;">Qui</th>
			<th colspan="2" style="text-align: center;">Sex</th>
			<th colspan="2" style="text-align: center;">Sáb</th>
 		</tr>
 		<tr>
			<th style="text-align: center;">Entrada</th><th style="text-align: center;">Saída</th>
			<th style="text-align: center;">Entrada</th><th style="text-align: center;">Saída</th>
			<th style="text-align: center;">Entrada</th><th style="text-align: center;">Saída</th>
			<th style="text-align: center;">Entrada</th><th style="text-align: center;">Saída</th>
			<th style="text-align: center;">Entrada</th><th style="text-align: center;">Saída</th>
			<th style="text-align: center;">Entrada</th><th style="text-align: center;">Saída</th>
 		</tr>
 	</thead>
 	<tbody>
 		<tr>
 			<th style="text-align: right;">Matutino:</th>
 			<th style="text-align: center;">
 				<h:inputText id="t1d2horainicio" value="#{estagioMBean.horarioMatutino['2'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t1d2horaFim" value="#{estagioMBean.horarioMatutino['2'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t1d3horainicio" value="#{estagioMBean.horarioMatutino['3'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t1d3horaFim" value="#{estagioMBean.horarioMatutino['3'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t1d4horainicio" value="#{estagioMBean.horarioMatutino['4'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t1d4horaFim" value="#{estagioMBean.horarioMatutino['4'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t1d5horainicio" value="#{estagioMBean.horarioMatutino['5'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t1d5horaFim" value="#{estagioMBean.horarioMatutino['5'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t1d6horainicio" value="#{estagioMBean.horarioMatutino['6'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t1d6horaFim" value="#{estagioMBean.horarioMatutino['6'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t1d7horainicio" value="#{estagioMBean.horarioMatutino['7'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t1d7horaFim" value="#{estagioMBean.horarioMatutino['7'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
 		</tr>
 		<tr>
 			<th style="text-align: right;">Vespertino:</th>
 			<th style="text-align: center;">
 				<h:inputText id="t2d2horainicio" value="#{estagioMBean.horarioVespertino['2'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t2d2horaFim" value="#{estagioMBean.horarioVespertino['2'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t2d3horainicio" value="#{estagioMBean.horarioVespertino['3'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t2d3horaFim" value="#{estagioMBean.horarioVespertino['3'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t2d4horainicio" value="#{estagioMBean.horarioVespertino['4'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t2d4horaFim" value="#{estagioMBean.horarioVespertino['4'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t2d5horainicio" value="#{estagioMBean.horarioVespertino['5'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t2d5horaFim" value="#{estagioMBean.horarioVespertino['5'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t2d6horainicio" value="#{estagioMBean.horarioVespertino['6'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t2d6horaFim" value="#{estagioMBean.horarioVespertino['6'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t2d7horainicio" value="#{estagioMBean.horarioVespertino['7'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t2d7horaFim" value="#{estagioMBean.horarioVespertino['7'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
 		</tr>
 		<tr>
 			<th style="text-align: right;">Noturno:</th>
 			<th style="text-align: center;">
 				<h:inputText id="t3d2horainicio" value="#{estagioMBean.horarioNoturno['2'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t3d2horaFim" value="#{estagioMBean.horarioNoturno['2'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t3d3horainicio" value="#{estagioMBean.horarioNoturno['3'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t3d3horaFim" value="#{estagioMBean.horarioNoturno['3'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t3d4horainicio" value="#{estagioMBean.horarioNoturno['4'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t3d4horaFim" value="#{estagioMBean.horarioNoturno['4'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t3d5horainicio" value="#{estagioMBean.horarioNoturno['5'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t3d5horaFim" value="#{estagioMBean.horarioNoturno['5'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t3d6horainicio" value="#{estagioMBean.horarioNoturno['6'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t3d6horaFim" value="#{estagioMBean.horarioNoturno['6'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			<th style="text-align: center;">
 				<h:inputText id="t3d7horainicio" value="#{estagioMBean.horarioNoturno['7'].horaInicio}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
			
			<th style="text-align: center;">
 				<h:inputText id="t3d7horaFim" value="#{estagioMBean.horarioNoturno['7'].horaFim}" size="5" maxlength="5" onkeypress="return(formataHora(this, event))">
					<f:convertDateTime pattern="HH:mm" />
				</h:inputText>
			</th>
 		</tr>
 	</tbody>
 </table>